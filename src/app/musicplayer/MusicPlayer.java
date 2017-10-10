package app.musicplayer;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.rookit.mongodb.DBManager;

import app.musicplayer.rookit.CurrentPlaylist;
import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.dm.MPTrack;
import app.musicplayer.util.Resources;
import app.musicplayer.view.MainController;
import app.musicplayer.view.NowPlayingController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class MusicPlayer extends Application {

	private static MainController mainController;
	private static MediaPlayer mediaPlayer;
	private static Timer timer;
	private static int timerCounter;
	private static int secondsPlayed;
	private static Object draggedItem;
	private static boolean isMuted = false;
	
	private static Stage stage;
	
	private static MusicPlayer current;
	
	public static MusicPlayer getCurrent() {
		return current;
	}

	public static void main(String[] args) {
		Application.launch(MusicPlayer.class);
	}

	private final RookitLibrary library;
	private final CurrentPlaylist nowPlaying;

	public MusicPlayer() {
		this.library = RookitLibrary.create(DBManager.open("localhost", 27039, "rookit"));
		nowPlaying = new CurrentPlaylist(library);
	}
	
	public RookitLibrary getLibrary() {
		return library;
	}

	@Override
	public void start(Stage stage) throws Exception {
		current = this;

		timer = new Timer();
		timerCounter = 0;
		secondsPlayed = 0;

		MusicPlayer.stage = stage;
		MusicPlayer.stage.setTitle("Music Player");
		MusicPlayer.stage.getIcons().add(new Image(this.getClass().getResource(Resources.IMG + "Icon.png").toString()));
		MusicPlayer.stage.setOnCloseRequest(event -> {
			Platform.exit();
			System.exit(0);
		});

		try {
			// Load main layout from fxml file.
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Resources.FXML + "SplashScreen.fxml"));
			VBox view = loader.load();

			// Shows the scene containing the layout.
			Scene scene = new Scene(view);
			stage.setScene(scene);
			stage.setMaximized(true);
			stage.show();

		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}

		Thread thread = new Thread(() -> {
			nowPlaying.addAll(library.streamTracks()
					.filter(track -> track.getPath() != null)
					.limit(50)
					.collect(Collectors.toList()));

			timer = new Timer();
			timerCounter = 0;
			secondsPlayed = 0;
			Media media = new Media(nowPlaying.getCurrentURI());
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.setVolume(0.5);
			mediaPlayer.setOnEndOfMedia(new SongSkipper());

			//            File imgFolder = new File(Resources.JAR + "/img");
			//            if (!imgFolder.exists()) {
			//
			//                Thread thread1 = new Thread(() -> {
			//                    library.getArtists().forEach(Artist::downloadArtistImage);
			//                });
			//
			//                Thread thread2 = new Thread(() -> {
			//                    library.getAlbums().forEach(Album::downloadArtwork);
			//                });
			//
			//                thread1.start();
			//                thread2.start();
			//            }

			// Calls the function to initialize the main layout.
			Platform.runLater(this::initMain);
		});

		thread.start();
	}

	/**
	 * Initializes the main layout.
	 */
	private void initMain() {
		try {
			// Load main layout from fxml file.
			FXMLLoader loader = new FXMLLoader(this.getClass().getResource(Resources.FXML + "Main.fxml"));
			BorderPane view = loader.load();

			// Shows the scene containing the layout.
			double width = stage.getScene().getWidth();
			double height = stage.getScene().getHeight();

			view.setPrefWidth(width);
			view.setPrefHeight(height);

			Scene scene = new Scene(view);
			stage.setScene(scene);

			// Gives the controller access to the music player main application.
			mainController = loader.getController();
			mediaPlayer.volumeProperty().bind(mainController.getVolumeSlider().valueProperty().divide(200));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class SongSkipper implements Runnable {
		@Override
		public void run() {
			skip();
		}
	}

	private class TimeUpdater extends TimerTask {
		private int length = (int) nowPlaying.getCurrent().getDuration()/1000 * 4;

		@Override
		public void run() {
			Platform.runLater(() -> {
				if (timerCounter < length) {
					if (++timerCounter % 4 == 0) {
						mainController.updateTimeLabels();
						secondsPlayed++;
					}
					if (!mainController.isTimeSliderPressed()) {
						mainController.updateTimeSlider();
					}
				}
			});
		}
	}

	/**
	 * Plays selected song.
	 */
	public void play() {
		if (mediaPlayer != null && !isPlaying()) {
			mediaPlayer.play();
			timer.scheduleAtFixedRate(new TimeUpdater(), 0, 250);
			mainController.updatePlayPauseIcon(true);
		}
	}

	/**
	 * Checks if a song is playing.
	 */
	public boolean isPlaying() {
		return mediaPlayer != null && MediaPlayer.Status.PLAYING.equals(mediaPlayer.getStatus());
	}

	/**
	 * Pauses selected song.
	 */
	public void pause() {
		if (isPlaying()) {
			mediaPlayer.pause();
			timer.cancel();
			timer = new Timer();
			mainController.updatePlayPauseIcon(false);
		}
	}

	public void seek(int seconds) {
		if (mediaPlayer != null) {
			mediaPlayer.seek(new Duration(seconds * 1000));
			timerCounter = seconds * 4;
			mainController.updateTimeLabels();
		}
	}

	/**
	 * Skips song.
	 */
	public void skip() {
		if (nowPlaying.hasNext()) {
			boolean isPlaying = isPlaying();
			mainController.updatePlayPauseIcon(isPlaying);
			setNowPlaying(nowPlaying.next());
			if (isPlaying) {
				play();
			}
		} else {
			mainController.updatePlayPauseIcon(false);
		}
	}

	public void back() {
		if (timerCounter > 20 || !nowPlaying.hasPrevious()) {
			mainController.initializeTimeSlider();
			seek(0);
		} else if(nowPlaying.hasPrevious()) {
			boolean isPlaying = isPlaying();
			setNowPlaying(nowPlaying.previous());
			if (isPlaying) {
				play();
			}
		}
	}

	public static void mute(boolean isMuted) {
		MusicPlayer.isMuted = !isMuted;
		if (mediaPlayer != null) {
			mediaPlayer.setMute(!isMuted);
		}
	}

	public void toggleLoop() {
		nowPlaying.setLoopActive(!nowPlaying.isLoopActive());
	}

	public boolean isLoopActive() {
		return nowPlaying.isLoopActive();
	}

	public void toggleShuffle() {
		nowPlaying.setShuffleActive(!nowPlaying.isShuffleActive());
		if (mainController.getSubViewController() instanceof NowPlayingController) {
			mainController.loadView("nowPlaying");
		}
	}

	public boolean isShuffleActive() {
		return nowPlaying.isShuffleActive();
	}

	public Stage getStage() {
		return stage;
	}

	/**
	 * Gets main controller object.
	 * @return MainController
	 */
	public static MainController getMainController() {
		return mainController;
	}

	/**
	 * Gets currently playing song list.
	 * @return arraylist of now playing songs
	 */
	public CurrentPlaylist getNowPlayingList() {
		return nowPlaying;
	}

	public void addSongToNowPlayingList(MPTrack song) {
		nowPlaying.add(song);
	}

	public void setNowPlayingList(List<MPTrack> list) {
		nowPlaying.clear();
		nowPlaying.addAll(list);
	}

	public void setNowPlaying(MPTrack song) {
		nowPlaying.markCurrentAsPlayed(secondsPlayed);
		final String uri = nowPlaying.skipTo(song);
		if(uri != null) {
			if (mediaPlayer != null) {
				mediaPlayer.stop();
			}
			if (timer != null) {
				timer.cancel();
			}
			timer = new Timer();
			timerCounter = 0;
			secondsPlayed = 0;
			Media media = new Media(uri);
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.volumeProperty().bind(mainController.getVolumeSlider().valueProperty().divide(200));
			mediaPlayer.setOnEndOfMedia(new SongSkipper());
			mediaPlayer.setMute(isMuted);
			mainController.updateNowPlayingButton();
			mainController.initializeTimeSlider();
			mainController.initializeTimeLabels();
			song.setPlaying(true);
		}
	}

	public MPTrack getNowPlaying() {
		return nowPlaying.getCurrent();
	}

	public String getTimePassed() {
		int secondsPassed = timerCounter / 4;
		int minutes = secondsPassed / 60;
		int seconds = secondsPassed % 60;
		return Integer.toString(minutes) + ":" + (seconds < 10 ? "0" + seconds : Integer.toString(seconds));
	}

	public String getTimeRemaining() {
		final long secondsPassed = timerCounter / 4;
		final long totalSeconds = getNowPlaying().getDuration();
		final long secondsRemaining = totalSeconds - secondsPassed;
		final long minutes = secondsRemaining / 60;
		final long seconds = secondsRemaining % 60;
		return Long.toString(minutes) + ":" + (seconds < 10 ? "0" + seconds : Long.toString(seconds));
	}

	public static void setDraggedItem(Object item) {
		draggedItem = item;
	}

	public Object getDraggedItem() {
		return draggedItem;
	}
}
