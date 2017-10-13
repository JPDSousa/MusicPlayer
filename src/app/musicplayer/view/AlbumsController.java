package app.musicplayer.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import app.musicplayer.MusicPlayer;
import app.musicplayer.rookit.Utils;
import app.musicplayer.rookit.dm.MPAlbum;
import app.musicplayer.rookit.dm.MPTrack;
import app.musicplayer.util.ClippedTableCell;
import app.musicplayer.util.ControlPanelTableCell;
import app.musicplayer.util.PlayingTableCell;
import app.musicplayer.util.SubView;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class AlbumsController implements Initializable, SubView {
	
    @FXML private ScrollPane gridBox;
	@FXML private FlowPane grid;
    @FXML private VBox songBox;
    @FXML private TableView<MPTrack> songTable;
    @FXML private TableColumn<MPTrack, Boolean> playingColumn;
    @FXML private TableColumn<MPTrack, String> titleColumn;
    @FXML private TableColumn<MPTrack, String> lengthColumn;
    @FXML private TableColumn<MPTrack, Integer> playsColumn;
    @FXML private Label artistLabel;
    @FXML private Label albumLabel;
    @FXML private Separator horizontalSeparator;
    @FXML private Separator verticalSeparator;
    
    private boolean isAlbumDetailCollapsed = true;
    
    // Initializes values used for animations.
    private double expandedHeight = 400;
    private double collapsedHeight = 0;
    
    // Initializes the index for the currently selected cell.
    private int currentCell;
    
    // Initializes the value of the x-coordinate for the currently selected cell.
    private double currentCellYCoordinate;
    
    private MPTrack selectedTrack;
    private MusicPlayer player;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		player = MusicPlayer.getCurrent();
		songTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		ObservableList<MPAlbum> albums = player.getLibrary().getAllAlbums();
		Collections.sort(albums);

        int limit = (albums.size() < 25) ? albums.size() : 25;

		for (int i = 0; i < limit; i++) {

            MPAlbum album = albums.get(i);
            grid.getChildren().add(createCell(album, i));
		}

        int rows = (albums.size() % 5 == 0) ? albums.size() / 5 : albums.size() / 5 + 1;
        
        // Sets the height and width of the grid to fill the screen.
        grid.prefHeightProperty().bind(gridBox.widthProperty().divide(5).add(16).multiply(rows));
        grid.prefWidthProperty().bind(gridBox.widthProperty());
        
		// Sets the song table to be invisible when the view is initialized.
        songBox.setVisible(false);
        
        gridBox.heightProperty().addListener((obs, oldValue, newValue) -> {
        	expandedHeight = newValue.doubleValue() / 2.0;
        	if (!isAlbumDetailCollapsed) {
        		songBox.setPrefHeight(expandedHeight);
        	}
        });

        new Thread(() -> {

        	try {
        		Thread.sleep(1000);
        	} catch (Exception ex) {
        		ex.printStackTrace();
        	}
        	
            for (int j = 25; j < albums.size(); j++) {
            	MPAlbum album = albums.get(j);
                int k = j;
                Platform.runLater(() -> {
                    grid.getChildren().add(createCell(album, k));
                });
            }
        }).start();
        
        // Sets preferred column width.
        titleColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.5));
        lengthColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.25));
        playsColumn.prefWidthProperty().bind(songTable.widthProperty().subtract(50).multiply(0.25));
        
        songTable.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
        	songTable.requestFocus();
        	event.consume();
        });
        
        // Sets the playing properties for the songs in the song table.
        songTable.setRowFactory(x -> {
            TableRow<MPTrack> row = new TableRow<MPTrack>();

            PseudoClass playing = PseudoClass.getPseudoClass("playing");

            ChangeListener<Boolean> changeListener = (obs, oldValue, newValue) -> {
                row.pseudoClassStateChanged(playing, newValue.booleanValue());
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
                	ArrayList<Integer> indices = new ArrayList<Integer>(sm.getSelectedIndices());
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

        horizontalSeparator.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override public void handle(MouseEvent e) {
            	
            	expandedHeight = player.getStage().getHeight() - e.getSceneY() - 75;
            	
            	if (expandedHeight > gridBox.getHeight() * 0.75) {	
                	expandedHeight = gridBox.getHeight() * 0.75;
                } else if (expandedHeight < gridBox.getHeight() * 0.25) {
                	expandedHeight = gridBox.getHeight() * 0.25;
                }
            	
            	songBox.setPrefHeight(expandedHeight);
                e.consume();
            }
        });
	}
	
    private VBox createCell(MPAlbum album, int index) {

        VBox cell = new VBox();
        Label title = new Label(album.getTitle());
        final Image artwork = Utils.getAlbumArtwork(player.getLibrary(), album);
		ImageView image = new ImageView(artwork);
        image.imageProperty().bind(album.artworkProperty());
        VBox imageBox = new VBox();

        title.setTextOverrun(OverrunStyle.CLIP);
        title.setWrapText(true);
        title.setPadding(new Insets(10, 0, 10, 0));
        title.setAlignment(Pos.TOP_LEFT);
        title.setPrefHeight(66);
        title.prefWidthProperty().bind(grid.widthProperty().subtract(100).divide(5).subtract(1));

        image.fitWidthProperty().bind(grid.widthProperty().subtract(100).divide(5).subtract(1));
        image.fitHeightProperty().bind(grid.widthProperty().subtract(100).divide(5).subtract(1));
        image.setPreserveRatio(true);
        image.setSmooth(true);

        imageBox.prefWidthProperty().bind(grid.widthProperty().subtract(100).divide(5).subtract(1));
        imageBox.prefHeightProperty().bind(grid.widthProperty().subtract(100).divide(5).subtract(1));
        imageBox.setAlignment(Pos.CENTER);
        imageBox.getChildren().add(image);

        cell.getChildren().addAll(imageBox, title);
        cell.setPadding(new Insets(10, 10, 10, 10));
        cell.getStyleClass().add("album-cell");
        cell.setAlignment(Pos.CENTER);
        cell.setOnMouseClicked(event -> {
        	
        	PseudoClass selected = PseudoClass.getPseudoClass("selected");
        	
        	// If the album detail is collapsed, expand it and populate song table.
        	if (isAlbumDetailCollapsed) {
        		
        		cell.pseudoClassStateChanged(selected, true);
        		
            	// Updates the index of the currently selected cell.
            	currentCell = index;
            	
        		// Shows song table, plays load animation and populates song table with album songs.
        		expandAlbumDetail();
        		expandAnimation.play();
        		
        		artistLabel.setText(album.getArtistsAsString());
        		albumLabel.setText(album.getTitle());
        		populateTrackTable(cell, album);
        		
        		// Else if album detail is expanded and opened album is reselected.
        	} else if (!isAlbumDetailCollapsed && index == currentCell) {
        		
        		cell.pseudoClassStateChanged(selected, false);
        		
        		// Plays the collapse animation to remove the song table.
        		collapseAnimation.play();
        		
        		// Else if album detail is expanded and a different album is selected on the same row.
        	} else if (!isAlbumDetailCollapsed && !(index == currentCell)
        			&& currentCellYCoordinate == cell.getBoundsInParent().getMaxY()) {
        		
        		for (Node child : grid.getChildren()) {
        			child.pseudoClassStateChanged(selected, false);
        		}
        		cell.pseudoClassStateChanged(selected, true);
        		
            	// Updates the index of the currently selected cell.
            	currentCell = index;
            	
            	// Plays load animation and populates song table with songs of newly selected album.
            	tableCollapseAnimation.setOnFinished(x -> {
            		artistLabel.setText(album.getArtistsAsString());
            		albumLabel.setText(album.getTitle());
            		populateTrackTable(cell, album);
            		expandAlbumDetail();
            		tableExpandAnimation.play();
            		tableCollapseAnimation.setOnFinished(y -> collapseAlbumDetail());
            	});
            	
            	tableCollapseAnimation.play();
        		
        		// Else if album detail is expanded and a different album is selected on a different row.
        	} else if (!isAlbumDetailCollapsed && !(index == currentCell)
        			&& !(currentCellYCoordinate == cell.getBoundsInParent().getMaxY())) {
        		
        		for (Node child : grid.getChildren()) {
        			child.pseudoClassStateChanged(selected, false);
        		}
        		cell.pseudoClassStateChanged(selected, true);
        		
            	// Updates the index of the currently selected cell.
            	currentCell = index;
            	
            	// Collapses the song table and then expands it in the appropriate row with songs on new album.
            	collapseAlbumDetail();
        		expandAlbumDetail();
        		// Plays load animation and populates song table with songs of newly selected album.
        		tableCollapseAnimation.setOnFinished(x -> {
        			artistLabel.setText(album.getArtistsAsString());
            		albumLabel.setText(album.getTitle());
            		populateTrackTable(cell, album);
            		expandAlbumDetail();
            		tableExpandAnimation.play();
            		tableCollapseAnimation.setOnFinished(y -> collapseAlbumDetail());
            	});
            	
            	tableCollapseAnimation.play();
        		
        	} else {
        		
        		for (Node child : grid.getChildren()) {
        			child.pseudoClassStateChanged(selected, false);
        		}
        		
        		// Plays the collapse animation to remove the song table.
        		collapseAnimation.play();
        	}
        	// Sets the cells max x value as the current cell x coordinate.
        	currentCellYCoordinate = cell.getBoundsInParent().getMaxY();
        });
        
        cell.setOnDragDetected(event -> {
        	PseudoClass pressed = PseudoClass.getPseudoClass("pressed");
        	cell.pseudoClassStateChanged(pressed, false);
        	Dragboard db = cell.startDragAndDrop(TransferMode.ANY);
        	ClipboardContent content = new ClipboardContent();
            content.putString("Album");
            db.setContent(content);
        	MusicPlayer.setDraggedItem(album);
        	db.setDragView(cell.snapshot(null, null), cell.widthProperty().divide(2).get(), cell.heightProperty().divide(2).get());
            event.consume();
        });
        
        return cell;
    }
    
    private void expandAlbumDetail() {
    	isAlbumDetailCollapsed = false;
    	songBox.setVisible(true);
    }
    
    private void collapseAlbumDetail() {
    	isAlbumDetailCollapsed = true;
    	songTable.getItems().clear();
    	songBox.setVisible(false);
    }
    
    private void populateTrackTable(VBox cell, MPAlbum selectedAlbum) { 	
    	// Retrieves albums songs and stores them as an observable list.
    	ObservableList<MPTrack> albumTracks = player.getLibrary().fromTracks(selectedAlbum.getTracks());
    	
        playingColumn.setCellFactory(x -> new PlayingTableCell<MPTrack, Boolean>());
        titleColumn.setCellFactory(x -> new ControlPanelTableCell<MPTrack, String>());
        lengthColumn.setCellFactory(x -> new ClippedTableCell<MPTrack, String>());
        playsColumn.setCellFactory(x -> new ClippedTableCell<MPTrack, Integer>());

        // Sets each column item.
        // TODO reflectiveness here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        playingColumn.setCellValueFactory(new PropertyValueFactory<MPTrack, Boolean>("playing"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<MPTrack, String>("title"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<MPTrack, String>("length"));
        playsColumn.setCellValueFactory(new PropertyValueFactory<MPTrack, Integer>("playCount"));
        
        // Adds songs to table.
        songTable.setItems(albumTracks);
        double height = (albumTracks.size() + 1) * 50 + 2;
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
        songTableLoadAnimation.play();
    }
    
    @Override
    public void play() {
    	
    	MPTrack song = selectedTrack;
        ObservableList<MPTrack> songList = songTable.getItems();
        if (player.isShuffleActive()) {
        	Collections.shuffle(songList);
        	songList.remove(song);
        	songList.add(0, song);
        }
        player.setNowPlayingList(songList);
        player.setNowPlaying(song);
        player.play();
    }
    
    @Override
    public void scroll(char letter) {
    	
	    int index = 0;
    	double cellHeight = 0;
    	ObservableList<Node> children = grid.getChildren();
    	
    	for (int i = 0; i < children.size(); i++) {
    		
    		VBox cell = (VBox) children.get(i);
    		cellHeight = cell.getHeight();
    		if (cell.getChildren().size() > 1) {
    			Label label = (Label) cell.getChildren().get(1);
        		char firstLetter = removeArticle(label.getText()).charAt(0);
        		if (firstLetter < letter) {
        			index++;
        		}	
    		}
    	}
    	
    	double row = (index / 5) * cellHeight;
    	double finalVvalue = row / (grid.getHeight() - gridBox.getHeight());
    	double startVvalue = gridBox.getVvalue();
    	
    	Animation scrollAnimation = new Transition() {
            {
                setCycleDuration(Duration.millis(500));
            }
            @Override
			protected void interpolate(double frac) {
                double vValue = startVvalue + ((finalVvalue - startVvalue) * frac);
                gridBox.setVvalue(vValue);
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
    
    @Override
	public MPTrack getSelectedSong() {
    	return selectedTrack;
    }
    
    // Animation to display song table when an album is clicked and the song table is collapsed.
    private Animation expandAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
        }
        @Override
		protected void interpolate(double frac) {
        	double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (frac);
            songBox.setPrefHeight(curHeight);
            songBox.setOpacity(frac);
        }
    };
    
    // Animation to hide song table when an album is clicked and the song table is expanded.
    private Animation collapseAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setOnFinished(x -> collapseAlbumDetail());
        }
        @Override
		protected void interpolate(double frac) {
        	double curHeight = collapsedHeight + (expandedHeight - collapsedHeight) * (1.0 - frac);
            songBox.setPrefHeight(curHeight);
            songBox.setOpacity(1.0 - frac);
            songTable.setMinHeight(1 - frac);
            songTable.setPrefHeight(1 - frac);
        }
    };
    
    private Animation tableCollapseAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
            setOnFinished(x -> collapseAlbumDetail());
        }
        @Override
		protected void interpolate(double frac) {
        	double curLocation = collapsedHeight + (expandedHeight - collapsedHeight) * (frac);
            artistLabel.setTranslateY(curLocation);
            albumLabel.setTranslateY(curLocation);
            verticalSeparator.setTranslateY(curLocation);
        	songTable.setTranslateY(curLocation);
        	artistLabel.setOpacity(1.0 - frac);
            albumLabel.setOpacity(1.0 - frac);
            verticalSeparator.setOpacity(1.0 - frac);
        	songTable.setOpacity(1.0 - frac);
        }
    };

    private Animation tableExpandAnimation = new Transition() {
        {
            setCycleDuration(Duration.millis(250));
        }
        @Override
		protected void interpolate(double frac) {
        	double curLocation = collapsedHeight + (expandedHeight - collapsedHeight) * (1.0 - frac);
        	artistLabel.setTranslateY(curLocation);
            albumLabel.setTranslateY(curLocation);
            verticalSeparator.setTranslateY(curLocation);
            songTable.setTranslateY(curLocation);
            artistLabel.setOpacity(frac);
            albumLabel.setOpacity(frac);
            verticalSeparator.setOpacity(frac);
        	songTable.setOpacity(frac);
        }
    };
}
