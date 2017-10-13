package app.musicplayer.audio;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.dm.MPTrack;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import static javafx.scene.media.MediaPlayer.Status.*;

@SuppressWarnings("javadoc")
public class MasterPlayer implements AudioPlayer {

	private static final Path AUDIO_TEMP = Paths.get("audio_temp");

	private MediaPlayer player;
	private final RookitLibrary library;
	private final AudioPlayerBuilder builder;

	MasterPlayer(RookitLibrary library, AudioPlayerBuilder builder) throws IOException {
		if(!Files.exists(AUDIO_TEMP)) {
			Files.createDirectory(AUDIO_TEMP);
		}
		this.library = library;
		this.builder = builder;
	}

	@Override
	public void load(MPTrack track) {
		final boolean wasMuted = isLoaded() && player.isMute();
		final Path path = getPath(track);
		final Media media = new Media(path.toUri().toString());
		player = new MediaPlayer(media);
		player.setVolume(0.5);
		player.setMute(wasMuted);
		builder.apply(player);
	}
	
	private Path getPath(MPTrack track) {
		final Path path;
		try {
			if(track.getPath() != null) {
				path = AUDIO_TEMP.resolve(track.getIdAsString() + ".mp3");
				if(!Files.exists(path)) {
					Files.copy(library.stream(track.getPath()), path);
				}
				return path;
			}
			throw new RuntimeException("Cannot find audio content for track: " + track);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean isLoaded() {
		return player != null;
	}

	@Override
	public void play() {
		if(!isPlaying()) {
			player.play();
		}
	}

	@Override
	public void pause() {
		if(isPlaying()) {
			player.pause();
		}
	}
	
	@Override
	public void stop() {
		if(isPlaying()) {
			player.stop();
			player.dispose();
		}
	}

	@Override
	public void seek(Duration duration) {
		if(isLoaded()) {
			player.seek(new javafx.util.Duration(duration.toMillis()));
			builder.getOnSeek().run();
		}
	}

	@Override
	public boolean isPlaying() {
		return isLoaded() && PLAYING.equals(player.getStatus());
	}

	@Override
	public void mute(boolean mute) {
		if(isLoaded()) {
			player.setMute(mute);
		}
	}

	@Override
	public Duration getCurrentTime() {
		return isLoaded() ? player.getCurrentTime() : null;
	}

	@Override
	public Duration getRemainingTime() {
		return isLoaded() ? player.getMedia().getDuration().subtract(getCurrentTime()) : null;
	}

	@Override
	public void close() throws IOException {
		stop();
		player.dispose();
		//FileUtils.deleteDirectory(AUDIO_TEMP.toFile());
	}
}
