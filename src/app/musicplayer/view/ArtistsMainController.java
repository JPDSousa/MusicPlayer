package app.musicplayer.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CountDownLatch;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import app.musicplayer.MusicPlayer;
import app.musicplayer.rookit.Utils;
import app.musicplayer.rookit.dm.MPAlbum;
import app.musicplayer.rookit.dm.MPArtist;
import app.musicplayer.rookit.dm.MPTrack;
import app.musicplayer.util.ClippedTableCell;
import app.musicplayer.util.ControlPanelTableCell;
import app.musicplayer.util.PlayingTableCell;
import app.musicplayer.util.SubView;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class ArtistsMainController implements Initializable, SubView {
	
    private class ArtistCell extends ListCell<MPArtist> {

        private HBox cell = new HBox();
        private ImageView artistImage = new ImageView();
        private Label title = new Label();
        private MPArtist artist;

        ArtistCell() {
            super();
            artistImage.setFitWidth(40);
            artistImage.setFitHeight(40);
            artistImage.setPreserveRatio(true);
            artistImage.setSmooth(true);
            artistImage.setCache(true);
            title.setTextOverrun(OverrunStyle.CLIP);
            cell.getChildren().addAll(artistImage, title);
            cell.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(artistImage, new Insets(0, 10, 0, 0));
            this.setPrefWidth(248);
            
            this.setOnMouseClicked(event -> artistList.getSelectionModel().select(artist));
            
            this.setOnDragDetected(event -> {
            	Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            	ClipboardContent content = new ClipboardContent();
                content.putString("Artist");
                db.setContent(content);
            	MusicPlayer.setDraggedItem(artist);
            	db.setDragView(this.snapshot(null, null), 125, 25);
            	event.consume();
            });
        }

        @Override
        protected void updateItem(MPArtist artist, boolean empty) {

            super.updateItem(artist, empty);
            this.artist = artist;

            if (empty){

                setGraphic(null);

            } else {
                title.setText(artist.getName());
                artistImage.imageProperty().bind(artist.imageProperty());
                setGraphic(cell);
            }
        }
    }

    private class AlbumCell extends ListCell<MPAlbum> {

        private ImageView albumArtwork = new ImageView();
        private MPAlbum album;

        AlbumCell() {
            super();
            setAlignment(Pos.CENTER);
            setPrefHeight(140);
            setPrefWidth(140);
            albumArtwork.setFitWidth(130);
            albumArtwork.setFitHeight(130);
            albumArtwork.setPreserveRatio(true);
            albumArtwork.setSmooth(true);
            albumArtwork.setCache(true);
            
            this.setOnMouseClicked(event -> albumList.getSelectionModel().select(album));
            
            this.setOnDragDetected(event -> {
            	Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            	ClipboardContent content = new ClipboardContent();
                content.putString("Album");
                db.setContent(content);
            	MusicPlayer.setDraggedItem(album);
            	db.setDragView(this.snapshot(null, null), 75, 75);
                event.consume();
            });
        }

        @Override
        protected void updateItem(MPAlbum album, boolean empty) {

            super.updateItem(album, empty);
            this.album = album;

            if (empty){

                setGraphic(null);

            } else {

                albumArtwork.setImage(Utils.getAlbumArtwork(player.getLibrary(), album));
                setGraphic(albumArtwork);
            }
        }
    }

    @FXML private ListView<MPArtist> artistList;
    @FXML private ListView<MPAlbum> albumList;
    @FXML private TableView<MPTrack> songTable;
    @FXML private TableColumn<MPTrack, Boolean> playingColumn;
    @FXML private TableColumn<MPTrack, String> titleColumn;
    @FXML private TableColumn<MPTrack, String> lengthColumn;
    @FXML private TableColumn<MPTrack, Integer> playsColumn;
    @FXML private Label artistLabel;
    @FXML private Label albumLabel;
    @FXML private Separator separator;
    @FXML private VBox subViewRoot;
    @FXML private ScrollPane scrollPane;
    @FXML private ScrollPane artistListScrollPane;

    private MPTrack selectedTrack;
    private MPAlbum selectedAlbum;
    private MPArtist selectedArtist;
    private double expandedHeight = 50;
    private double collapsedHeight = 0;
    private CountDownLatch loadedLatch;
    
    private MusicPlayer player;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	player = MusicPlayer.getCurrent();
    	songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    	
    	loadedLatch = new CountDownLatch(1);
    	
    	artistLoadAnimation.setOnFinished(x -> loadedLatch.countDown());
    	
    	albumLoadAnimation.setOnFinished(x -> loadedLatch.countDown());
    	
    	titleColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.5));
        lengthColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.25));
        playsColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.25));

        playingColumn.setCellFactory(x -> new PlayingTableCell<>());
        titleColumn.setCellFactory(x -> new ControlPanelTableCell<>());
        lengthColumn.setCellFactory(x -> new ClippedTableCell<>());
        playsColumn.setCellFactory(x -> new ClippedTableCell<>());

        playingColumn.setCellValueFactory(new PropertyValueFactory<>("playing"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        playsColumn.setCellValueFactory(new PropertyValueFactory<>("playCount"));

        albumList.setCellFactory(listView -> new AlbumCell());
        artistList.setCellFactory(listView -> new ArtistCell());
        
        artistList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
        
        albumList.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> event.consume());
        
        songTable.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        	songTable.requestFocus();
        	event.consume();
        });

        ObservableList<MPArtist> artists = player.getLibrary().getAllArtists();
        Collections.sort(artists);
        
        artistList.setItems(artists);

        artistList.setOnMouseClicked(event -> {

            if (event.getClickCount() == 2) {
                ObservableList<MPTrack> songs = FXCollections.observableArrayList();
                ObservableList<MPAlbum> albums = FXCollections.observableArrayList();
                final Iterable<MPAlbum> artistAlbums = player.getLibrary().getArtistAlbums(selectedArtist);
                for (MPAlbum album : artistAlbums) {
                    albums.add(album);
                    album.getMPTracks().forEach(songs::add);
                }
                
                if (player.isShuffleActive()) {
                	Collections.shuffle(songs);
                } else {
                	// orders by album name, and then by track name
//                    Collections.sort(songs, (first, second) -> {
//                        MPAlbum firstAlbum = albums.stream().filter(x -> x.getTitle().equals(first.getAlbum())).findFirst().get();
//                        MPAlbum secondAlbum = albums.stream().filter(x -> x.getTitle().equals(second.getAlbum())).findFirst().get();
//                        if (firstAlbum.compareTo(secondAlbum) != 0) {
//                            return firstAlbum.compareTo(secondAlbum);
//                        }
//						return first.compareTo(second);
//                    });
                }

                MPTrack song = songs.get(0);
                player.setNowPlayingList(songs);
                player.setNowPlaying(song);
                player.play();

            } else {
                	
            	Task<Void> task = new Task<Void>() {
            		@Override protected Void call() throws Exception {
    	        		Platform.runLater(() -> {
    	        			subViewRoot.setVisible(false);
    	        			selectedArtist = artistList.getSelectionModel().getSelectedItem();
                            showAllTracks(selectedArtist, false);
                            artistLabel.setText(selectedArtist.getName());
                            albumList.setPrefWidth(albumList.getItems().size() * 150 + 2);
                            albumList.setMaxWidth(albumList.getItems().size() * 150 + 2);
                            albumList.scrollTo(0);
    	        		});
    		        	return null;
    	        	}
            	};
            	
            	task.setOnSucceeded(x -> Platform.runLater(() -> {
                    subViewRoot.setVisible(true);
                    artistLoadAnimation.play();
                }));
            	
            	Thread thread = new Thread(task);

            	artistUnloadAnimation.setOnFinished(x -> thread.start());
            	
            	artistUnloadAnimation.play();
            }
        });

        albumList.setOnMouseClicked(event -> {

            MPAlbum album = albumList.getSelectionModel().getSelectedItem();

            if (event.getClickCount() == 2) {

                if (album != selectedAlbum) {
                    selectAlbum(album);
                }

                List<MPTrack> songs = Lists.newArrayList(selectedAlbum.getMPTracks());
                if (player.isShuffleActive()) {
                	Collections.shuffle(songs);
                } else {
                	Collections.sort(songs);
                }

                player.setNowPlayingList(songs);
                player.setNowPlaying(songs.get(0));
                player.play();

            } else {
            	
            	Task<Void> task = new Task<Void>() {
            		@Override protected Void call() throws Exception {
    	        		Platform.runLater(() -> {
    	        			songTable.setVisible(false);
    	        			selectAlbum(album);
    	        		});
    		        	return null;
    	        	}                		
            	};
            	
            	task.setOnSucceeded(x -> Platform.runLater(() -> {
                    songTable.setVisible(true);
                    albumLoadAnimation.play();
                }));
            	
            	Thread thread = new Thread(task);

            	albumUnloadAnimation.setOnFinished(x -> thread.start());
            	
            	albumUnloadAnimation.play();
            }
        });

        songTable.setRowFactory(x -> {

            TableRow<MPTrack> row = new TableRow<>();

            PseudoClass playing = PseudoClass.getPseudoClass("playing");

            ChangeListener<Boolean> changeListener = (obs, oldValue, newValue) -> {
                row.pseudoClassStateChanged(playing, newValue);
            };

            row.itemProperty().addListener((obs, previousTrack, currentTrack) -> {
            	if (previousTrack != null) {
            		previousTrack.playingProperty().removeListener(changeListener);
            	}
                if (currentTrack != null) {
                    currentTrack.playingProperty().addListener(changeListener);
                    row.pseudoClassStateChanged(playing, currentTrack.isPlaying());
                } else {
                    row.pseudoClassStateChanged(playing, false);
                }
            });

            row.setOnMouseClicked(event -> {
            	TableViewSelectionModel<MPTrack> sm = songTable.getSelectionModel();
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    play();
                } else if (event.isShiftDown()) {
                	ArrayList<Integer> indices = new ArrayList<>(sm.getSelectedIndices());
                	if (indices.size() < 1) {
                		if (indices.contains(row.getIndex())) {
                    		sm.clearSelection(row.getIndex());
                    	} else {
                    		sm.select(row.getItem());
                    	}
                	} else {
                		sm.clearSelection();
	                	indices.sort((first, second) -> first.compareTo(second));
	                	int max = indices.get(indices.size() - 1);
	                	int min = indices.get(0);
	                	if (min < row.getIndex()) {
	                		for (int i = min; i <= row.getIndex(); i++) {
	                			sm.select(i);
	                		}
	                	} else {
	                		for (int i = row.getIndex(); i <= max; i++) {
	                			sm.select(i);
	                		}
	                	}
                	}
                	
                } else if (event.isControlDown()) {
                	if (sm.getSelectedIndices().contains(row.getIndex())) {
                		sm.clearSelection(row.getIndex());
                	} else {
                		sm.select(row.getItem());
                	}
                } else {
                	if (sm.getSelectedIndices().size() > 1) {
                		sm.clearSelection();
                    	sm.select(row.getItem());
                	} else if (sm.getSelectedIndices().contains(row.getIndex())) {
                		sm.clearSelection();
                	} else {
                		sm.clearSelection();
                    	sm.select(row.getItem());
                	}
                }
            });
            
            row.setOnDragDetected(event -> {
            	Dragboard db = row.startDragAndDrop(TransferMode.ANY);
            	ClipboardContent content = new ClipboardContent();
            	if (songTable.getSelectionModel().getSelectedIndices().size() > 1) {
            		content.putString("List");
                    db.setContent(content);
                	MusicPlayer.setDraggedItem(songTable.getSelectionModel().getSelectedItems());
            	} else {
            		content.putString("Track");
                    db.setContent(content);
                	MusicPlayer.setDraggedItem(row.getItem());
            	}
            	ImageView image = new ImageView(row.snapshot(null, null));
            	Rectangle2D rectangle = new Rectangle2D(0, 0, 250, 50);
            	image.setViewport(rectangle);
            	db.setDragView(image.snapshot(null, null), 125, 25);
                event.consume();
            });

            return row ;
        });
        
        songTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
        	if (oldSelection != null) {
        		oldSelection.setSelected(false);
        	}
        	if (newSelection != null && songTable.getSelectionModel().getSelectedIndices().size() == 1) {
        		newSelection.setSelected(true);
        		selectedTrack = newSelection;
        	}
        });
        
        // Plays selected song when enter key is pressed.
        songTable.setOnKeyPressed(event -> {
        	if (event.getCode().equals(KeyCode.ENTER)) {
        		play();
        	}
        });
        
        artistList.setMinHeight(0);
        artistList.setPrefHeight(0);
        double height = artists.size() * 50;
        Animation artistListLoadAnimation = new Transition() {
        	{
        		setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_BOTH);
        	}
        	
        	@Override
			protected void interpolate(double frac) {
        		artistList.setMinHeight(frac * height);
        		artistList.setPrefHeight(frac * height);
        	}
        };
        artistListLoadAnimation.play();
    }

    void selectAlbum(MPAlbum album) {

        if (selectedAlbum == album) {

            albumList.getSelectionModel().clearSelection();
            showAllTracks(artistList.getSelectionModel().getSelectedItem(), false);

        } else {
        	
        	if (selectedTrack != null) {
        		selectedTrack.setSelected(false);
        	}
        	selectedTrack = null;
            selectedAlbum = album;
            albumList.getSelectionModel().select(selectedAlbum);
            ObservableList<MPTrack> songs = FXCollections.observableArrayList();
            album.getMPTracks().forEach(songs::add);
            Collections.sort(songs);
            songTable.getSelectionModel().clearSelection();
            songTable.setItems(songs);
            scrollPane.setVvalue(0);
            albumLabel.setText(album.getTitle());
            songTable.setMinHeight(0);
            songTable.setPrefHeight(0);
            songTable.setVisible(true);
            double height = (songs.size() + 1) * 50 + 2;
            Animation songTableLoadAnimation = new Transition() {
            	{
            		setCycleDuration(Duration.millis(250));
                    setInterpolator(Interpolator.EASE_BOTH);
            	}
            	
            	@Override
				protected void interpolate(double frac) {
            		songTable.setMinHeight(frac * height);
                    songTable.setPrefHeight(frac * height);
            	}
            };
            new Thread(() -> {
            	try {
					loadedLatch.await();
					loadedLatch = new CountDownLatch(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
            	songTableLoadAnimation.play();
            }).start();
        }
    }
    
    void selectArtist(MPArtist artist) {
    	
    	selectedArtist = artist;
        artistList.getSelectionModel().select(artist);
        CountDownLatch latch = new CountDownLatch(1);
        artistListScrollPane.heightProperty().addListener((x, y, z) -> {
        	if (z.doubleValue() != 0) {
        		latch.countDown();
        	}
        });
        new Thread(() -> {
            try {
				latch.await();
				int selectedCell = artistList.getSelectionModel().getSelectedIndex();
	            double vValue = (selectedCell * 50) / (player.getLibrary().streamArtists().count() * 50 - artistListScrollPane.getHeight());
	            artistListScrollPane.setVvalue(vValue);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }).start();
        showAllTracks(artist, true);
        final long nAlbums = player.getLibrary().getArtistAlbums(selectedArtist).size();
        albumList.setPrefWidth(nAlbums * 150 + 2);
        albumList.setMaxWidth(nAlbums * 150 + 2);
        artistLabel.setText(artist.getName());
        separator.setVisible(true);
    }
    
    void selectTrack(MPTrack song) {
    	
    	new Thread(() -> {
            try {
				loadedLatch.await();
				loadedLatch = new CountDownLatch(1);
				Platform.runLater(() -> {
					songTable.getSelectionModel().select(song);
			        scrollPane.requestFocus();
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
        }).start();
    }
    
    @Override
	public MPTrack getSelectedSong() {
    	return selectedTrack;
    }

    private void showAllTracks(MPArtist artist, boolean fromMainController) {

        ObservableList<MPAlbum> albums = FXCollections.observableArrayList();
        ObservableList<MPTrack> songs = FXCollections.observableArrayList();
        final ObservableList<MPAlbum> artistAlbums = player.getLibrary().getArtistAlbums(artist);

        for (MPAlbum album : artistAlbums) {
            albums.add(album);
            album.getMPTracks().forEach(songs::add);
        }

        // sort songs by album title, then by title
//        Collections.sort(songs, (first, second) -> {
//            MPAlbum firstAlbum = albums.stream().filter(x -> x.getTitle().equals(first.getAlbum())).findFirst().get();
//            MPAlbum secondAlbum = albums.stream().filter(x -> x.getTitle().equals(second.getAlbum())).findFirst().get();
//            if (firstAlbum.compareTo(secondAlbum) != 0) {
//                return firstAlbum.compareTo(secondAlbum);
//            }
//			return first.compareTo(second);
//        });

        Collections.sort(albums);

        if (selectedTrack != null) {
        	selectedTrack.setSelected(false);
        }
        selectedTrack = null;
    	selectedAlbum = null;
        albumList.getSelectionModel().clearSelection();
        albumList.setItems(albums);
        songTable.setItems(songs);
        songTable.getSelectionModel().clearSelection();
        scrollPane.setVvalue(0);
        albumLabel.setText("All Tracks");
        songTable.setMinHeight(0);
        songTable.setPrefHeight(0);
        songTable.setVisible(true);
        double height = (songs.size() + 1) * 50 + 2;
        Animation songTableLoadAnimation = new Transition() {
        	{
        		setCycleDuration(Duration.millis(250));
                setInterpolator(Interpolator.EASE_BOTH);
        	}
        	
        	@Override
			protected void interpolate(double frac) {
        		songTable.setMinHeight(frac * height);
                songTable.setPrefHeight(frac * height);
        	}
        };
        
        songTableLoadAnimation.setOnFinished(x -> loadedLatch.countDown());
        
        new Thread(() -> {
        	try {
        		if (fromMainController) {
        			MusicPlayer.getMainController().getLatch().await();
        			MusicPlayer.getMainController().resetLatch();
        		} else {
        			loadedLatch.await();
        			loadedLatch = new CountDownLatch(1);
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
			songTableLoadAnimation.play();
        }).start();
    }
    
    @Override
    public void play() {
    	
    	MPTrack song = selectedTrack;
        List<MPTrack> songs = new ArrayList<>();

        if (selectedAlbum != null) {
        	selectedAlbum.getMPTracks().forEach(songs::add);
        } else {
            for (MPAlbum album : player.getLibrary().getArtistAlbums(selectedArtist)) {
            	album.getMPTracks().forEach(songs::add);
            }
        }
        
        if (player.isShuffleActive()) {
        	Collections.shuffle(songs);
        	songs.remove(song);
        	songs.add(0, song);
        } else {
        	// sort by album title, then by title
//        	Collections.sort(songs, (first, second) -> {
//                MPAlbum firstAlbum = Library.getDefault().getAlbum(first.getAlbum());
//                MPAlbum secondAlbum = Library.getDefault().getAlbum(second.getAlbum());
//                if (firstAlbum.compareTo(secondAlbum) != 0) {
//                    return firstAlbum.compareTo(secondAlbum);
//                }
//				return first.compareTo(second);
//            });
        }

        player.setNowPlayingList(songs);
        player.setNowPlaying(song);
        player.play();
    }
    
    @Override
    public void scroll(char letter) {
    	
    	ObservableList<MPArtist> artistListItems = artistList.getItems();
    	
    	double selectedCell = 0;

        for (MPArtist artist : artistListItems) {
            // Removes article from artist title and compares it to selected letter.
            String artistTitle = artist.getName();
            char firstLetter = removeArticle(artistTitle).charAt(0);
            if (firstLetter < letter) {
                selectedCell++;
            }
        }
    	
    	double startVvalue = artistListScrollPane.getVvalue();
    	double finalVvalue = selectedCell * 50 / (player.getLibrary().streamArtists().count() * 50 - artistListScrollPane.getHeight());
    	
    	Animation scrollAnimation = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
            }
            @Override
			protected void interpolate(double frac) {
                double vValue = startVvalue + ((finalVvalue - startVvalue) * frac);
                artistListScrollPane.setVvalue(vValue);
            }
        };
        scrollAnimation.play();
    }
    
    private String removeArticle(String title) {

        String arr[] = title.split(" ", 2);

        if (arr.length < 2) {
            return title;
        }
		String firstWord = arr[0];
		String theRest = arr[1];

		switch (firstWord) {
		    case "A":
		    case "An":
		    case "The":
		        return theRest;
		    default:
		        return title;
		}
    }
    
    private Animation artistLoadAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setInterpolator(Interpolator.EASE_BOTH);
        }
        @Override
		protected void interpolate(double frac) {
            double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (frac);
            subViewRoot.setTranslateY(expandedHeight - curHeight);
            subViewRoot.setOpacity(frac);
        }
    };
    
    private Animation artistUnloadAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setInterpolator(Interpolator.EASE_BOTH);
        }
        @Override
		protected void interpolate(double frac) {
            double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (1 - frac);
            subViewRoot.setTranslateY(expandedHeight - curHeight);
            subViewRoot.setOpacity(1 - frac);
        }
    };

    private Animation albumLoadAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setInterpolator(Interpolator.EASE_BOTH);
        }
        @Override
		protected void interpolate(double frac) {
            double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (frac);
            songTable.setTranslateY(expandedHeight - curHeight);
            songTable.setOpacity(frac);
        }
    };
    
    private Animation albumUnloadAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setInterpolator(Interpolator.EASE_BOTH);
        }
        @Override
		protected void interpolate(double frac) {
            double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (1 - frac);
            songTable.setTranslateY(expandedHeight - curHeight);
            songTable.setOpacity(1 - frac);
            songTable.setMinHeight(1 - frac);
            songTable.setPrefHeight(1 - frac);
        }
    };
}
