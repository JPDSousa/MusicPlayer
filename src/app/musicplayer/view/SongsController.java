package app.musicplayer.view;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import org.rookit.dm.utils.PrintUtils;

import com.google.common.collect.Lists;

import app.musicplayer.MusicPlayer;
import app.musicplayer.rookit.dm.MPTrack;
import app.musicplayer.util.ClippedTableCell;
import app.musicplayer.util.ControlPanelTableCell;
import app.musicplayer.util.PlayingTableCell;
import app.musicplayer.util.SubView;
import javafx.animation.Animation;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class SongsController implements Initializable, SubView {

	@FXML private TableView<MPTrack> tableView;
	@FXML private TableColumn<MPTrack, Boolean> playingColumn;
	@FXML private TableColumn<MPTrack, String> titleColumn;
	@FXML private TableColumn<MPTrack, String> artistColumn;
	@FXML private TableColumn<MPTrack, String> lengthColumn;
	@FXML private TableColumn<MPTrack, Number> playsColumn;

	// Initializes table view scroll bar.
	private ScrollBar scrollBar;

	// Keeps track of which column is being used to sort table view and in what order (ascending or descending)
	private String currentSortColumn = "titleColumn";
	private String currentSortOrder = null;

	private MPTrack selectedSong;

	private MusicPlayer player;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		player = MusicPlayer.getCurrent();
		tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		titleColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.26));
		artistColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.26));
		lengthColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.11));
		playsColumn.prefWidthProperty().bind(tableView.widthProperty().subtract(50).multiply(0.11));

		playingColumn.setCellFactory(x -> new PlayingTableCell<>());
		titleColumn.setCellFactory(x -> new ControlPanelTableCell<>());
		artistColumn.setCellFactory(x -> new ClippedTableCell<>());
		lengthColumn.setCellFactory(x -> new ClippedTableCell<>());
		playsColumn.setCellFactory(x -> new ClippedTableCell<>());

		playingColumn.setCellValueFactory(param -> param.getValue().playingProperty());
		titleColumn.setCellValueFactory(param -> param.getValue().titleProperty());
		artistColumn.setCellValueFactory(param -> param.getValue().artistsProperty());
		lengthColumn.setCellValueFactory(param -> param.getValue().lenghProperty());
		playsColumn.setCellValueFactory(param -> param.getValue().playsProperty());

		lengthColumn.setSortable(false);
		playsColumn.setSortable(false);

		tableView.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
			tableView.requestFocus();
			event.consume();
		});

		// Retrieves the list of songs in the library, sorts them, and adds them to the table.
		ObservableList<MPTrack> songs = FXCollections.observableArrayList(Lists.newArrayList(player.getNowPlayingList().getPlaylist()));
		Collections.sort(songs);
		tableView.setItems(songs);
		tableView.setRowFactory(x -> {
			final TableRow<MPTrack> row = new TableRow<>();
			final PseudoClass playing = PseudoClass.getPseudoClass("playing");

			ChangeListener<Boolean> changeListener = (obs, oldValue, newValue) ->
			row.pseudoClassStateChanged(playing, newValue);

			row.itemProperty().addListener((obs, previousSong, currentSong) -> {
				if (previousSong != null) {
					previousSong.playingProperty().removeListener(changeListener);
				}
				if (currentSong != null) {
					currentSong.playingProperty().addListener(changeListener);
					row.pseudoClassStateChanged(playing, currentSong.isPlaying());
				} else {
					row.pseudoClassStateChanged(playing, false);
				}
			});

			row.setOnMouseClicked(event -> {
				TableViewSelectionModel<MPTrack> sm = tableView.getSelectionModel();
				if (event.getClickCount() == 2 && !row.isEmpty()) {
					
					play();
				} else if (event.isShiftDown()) {
					List<Integer> indices = new ArrayList<>(sm.getSelectedIndices());
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
				if (tableView.getSelectionModel().getSelectedIndices().size() > 1) {
					content.putString("List");
					db.setContent(content);
					MusicPlayer.setDraggedItem(tableView.getSelectionModel().getSelectedItems());
				} else {
					content.putString("Song");
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

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (oldSelection != null) {
				oldSelection.setSelected(false);
			}
			if (newSelection != null /*&& tableView.getSelectionModel().getSelectedIndices().size() == 1*/) {
				newSelection.setSelected(true);
				selectedSong = newSelection;
			}
		});

		// Plays selected song when enter key is pressed.
		tableView.setOnKeyPressed(event -> {
			if (event.getCode().equals(KeyCode.ENTER)) {
				play();
			}
		});

		titleColumn.setComparator((x, y) -> {

			if (x == null && y == null) {
				return 0;
			} else if (x == null) {
				return 1;
			} else if (y == null) {
				return -1;
			}

			//        	MPTrack first = library.getSong(x);
			//        	MPTrack second = library.getSong(y);

			return x.compareTo(y);
		});

		artistColumn.setComparator(new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		//        artistColumn.setComparator((first, second) -> library.getArtist(first).compareTo(library.getArtist(second)));

		//        albumColumn.setComparator((first, second) -> library.getAlbum(first).compareTo(library.getAlbum(second)));
	}

	@Override
	public void play() {
		MPTrack song = selectedSong;
		ObservableList<MPTrack> songList = tableView.getItems();
		if (player.isShuffleActive()) {
			Collections.shuffle(songList);
			songList.remove(song);
			songList.add(0, song);
		}
		player.setNowPlayingList(songList);
		player.skipTo(song);
		player.play();
	}

	@Override
	public void scroll(char letter) {

		if (tableView.getSortOrder().size() > 0) {
			currentSortColumn = tableView.getSortOrder().get(0).getId();
			currentSortOrder = tableView.getSortOrder().get(0).getSortType().toString().toLowerCase();
		}

		// Retrieves songs from table.
		ObservableList<MPTrack> songTableItems = tableView.getItems();
		// Initializes counter for cells. Used to determine what cell to scroll to.
		double selectedCell = 0;
		int selectedLetterCount = 0;

		// Retrieves the table view scroll bar.
		if (scrollBar == null) {
			scrollBar = (ScrollBar) tableView.lookup(".scroll-bar");
		}

		switch (currentSortColumn) {
		case "titleColumn":
			for (MPTrack song : songTableItems) {
				// Gets song title and compares first letter to selected letter.
				String songTitle = song.getTitle().toString();
				try {
					char firstLetter = songTitle.charAt(0);
					if (firstLetter < letter) {
						selectedCell++;
					} else if (firstLetter == letter) {
						selectedLetterCount++;
					}
				} catch (NullPointerException npe) {
					System.out.println("Null Song Title");
				}

			}
			break;
		case "artistColumn":
			for (MPTrack song : songTableItems) {
				// Removes article from song artist and compares it to selected letter.
				String songArtist = PrintUtils.getIterableAsString(song.getMainArtists(), ", ");
				try {
					char firstLetter = removeArticle(songArtist).charAt(0);
					if (firstLetter < letter) {
						selectedCell++;
					} else if (firstLetter == letter) {
						selectedLetterCount++;
					}
				} catch (NullPointerException npe) {
					System.out.println("Null Song Artist");
				}
			}
			break;
		case "albumColumn":
			for (MPTrack song : songTableItems) {
				// Removes article from song album and compares it to selected letter.
				//                    String songAlbum = song.getAlbum();
				String songAlbum = song.getTitle().toString();
				try {
					char firstLetter = removeArticle(songAlbum).charAt(0);
					if (firstLetter < letter) {
						selectedCell++;
					} else if (firstLetter == letter) {
						selectedLetterCount++;
					}
				} catch (NullPointerException npe) {
					System.out.println("Null Song Album");
				}
			}
			break;
		}

		double startVvalue = scrollBar.getValue();
		double finalVvalue;

		if ("descending".equals(currentSortOrder)) {
			finalVvalue = 1 - (((selectedCell + selectedLetterCount) * 50 - scrollBar.getHeight()) /
					(songTableItems.size() * 50 - scrollBar.getHeight()));
		} else {
			finalVvalue = selectedCell * 50 / (songTableItems.size() * 50 - scrollBar.getHeight());
		}

		Animation scrollAnimation = new Transition() {
			{
				setCycleDuration(Duration.millis(500));
			}
			@Override
			protected void interpolate(double frac) {
				double vValue = startVvalue + ((finalVvalue - startVvalue) * frac);
				scrollBar.setValue(vValue);
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
		return selectedSong;
	}
}
