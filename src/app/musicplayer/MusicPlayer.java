package app.musicplayer;

import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.rookit.mongodb.DBManager;

import app.musicplayer.audio.AudioPlayer;
import app.musicplayer.audio.AudioPlayerBuilder;
import app.musicplayer.rookit.CurrentPlaylist;
import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.Utils;
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
import javafx.stage.Stage;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class MusicPlayer extends Application {

	private static final Duration TIMER_GAP = new Duration(250);
	
	private static MainController mainController;
	private static Object draggedItem;

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
	private AudioPlayer player;
	private Timer timer;

	public MusicPlayer() {
		this.library = RookitLibrary.create(DBManager.open("localhost", 27039, "rookit"));
		nowPlaying = new CurrentPlaylist();
		nowPlaying.addAll(library.streamTracks()
				.filter(track -> track.getPath() != null)
				.limit(50)
				.collect(Collectors.toList()));
		timer = new Timer();
	}

	private AudioPlayer buildAudioPlayer() {
		return AudioPlayerBuilder.newBuilder()
				.withLibrary(library)
				.onEnd(new OnEndNext())
				.onPlay(new OnPlay())
				.onPause(new OnPause())
				.onSeek(new OnSeek())
				.bindVolumeTo(mainController.getVolumeSlider().valueProperty().divide(200))
				.build();
	}

	public RookitLibrary getLibrary() {
		return library;
	}

	@Override
	public void start(Stage stage) throws Exception {
		current = this;

		MusicPlayer.stage = stage;
		MusicPlayer.stage.setTitle("Music Player");
		MusicPlayer.stage.getIcons().add(new Image(this.getClass().getResource(Resources.IMG + "Icon.png").toString()));
		MusicPlayer.stage.setOnCloseRequest(event -> {
			Platform.exit();
			try {
				player.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
			player = buildAudioPlayer();
			player.load(nowPlaying.getCurrent());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private class OnEndNext implements Runnable {
		@Override
		public void run() {
			skip();
		}
	}

	private class OnSeek implements Runnable {
		@Override
		public void run() {
			mainController.updateTimeLabels();
		}
	}

	private class OnPlay implements Runnable {
		@Override
		public void run() {
			timer.scheduleAtFixedRate(new TimeUpdater(), 0, Math.round(TIMER_GAP.toMillis()));
			mainController.updatePlayPauseIcon(true);
		}
	}

	private class OnPause implements Runnable {
		@Override
		public void run() {
			timer.cancel();
			timer = new Timer();
			mainController.updatePlayPauseIcon(false);
		}
	}

	private class TimeUpdater extends TimerTask {
		private Duration length = new Duration(nowPlaying.getCurrent().getDuration());

		@Override
		public void run() {
			Platform.runLater(() -> {
				final Duration current = player.getCurrentTime();
				if (current.lessThan(length)) {
					final double seconds = current.toSeconds();
					if (seconds - Math.round(seconds) < TIMER_GAP.toSeconds()) {
						mainController.updateTimeLabels();
					}
					if (!mainController.isTimeSliderPressed()) {
						mainController.updateTimeSlider();
					}
				}
			});
		}
	}
	
	public void mute(boolean isMuted) {
		player.mute(isMuted);
	}

	public void seek(int seconds) {
		player.seek(new Duration(seconds*1000));
	}

	public void play() {
		player.play();
	}

	public void pause() {
		player.pause();
	}

	/**
	 * Skips song.
	 */
	public void skip() {
		if (nowPlaying.hasNext()) {
			boolean isPlaying = player.isPlaying();
			mainController.updatePlayPauseIcon(isPlaying);
			skipTo(nowPlaying.next());
			if (isPlaying) {
				player.play();
			}
		} else {
			mainController.updatePlayPauseIcon(false);
		}
	}

	public void back() {
		boolean isPlaying = player.isPlaying();
		if (player.getCurrentTime().toMillis() > 20 || !nowPlaying.hasPrevious()) {
			skipTo(nowPlaying.getCurrent());
		} else if(nowPlaying.hasPrevious()) {
			skipTo(nowPlaying.previous());
		}
		if (isPlaying) {
			player.play();
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

	public boolean isPlaying() {
		return player.isPlaying();
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

	public void skipTo(MPTrack track) {
		nowPlaying.markCurrentAsPlayed(player.getCurrentTime());
		player.stop();
		nowPlaying.skipTo(track);
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		player.load(track);
		mainController.updateNowPlayingButton();
		mainController.initializeTimeSlider();
		mainController.initializeTimeLabels();
		track.setPlaying(true);
	}

	public MPTrack getNowPlaying() {
		return nowPlaying.getCurrent();
	}

	public String getTimePassed() {
		return Utils.duration2ClockString(player.getCurrentTime());
	}

	public String getTimeRemaining() {
		return Utils.duration2ClockString(player.getRemainingTime());
	}

	public static void setDraggedItem(Object item) {
		draggedItem = item;
	}

	public Object getDraggedItem() {
		return draggedItem;
	}
}
