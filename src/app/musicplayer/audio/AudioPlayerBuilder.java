package app.musicplayer.audio;

import java.io.IOException;

import app.musicplayer.rookit.RookitLibrary;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.media.MediaPlayer;

@SuppressWarnings("javadoc")
public class AudioPlayerBuilder {
	
	public static AudioPlayerBuilder newBuilder() {
		return new AudioPlayerBuilder();
	}
	
	private RookitLibrary library;
	private DoubleBinding volumeBinding;
	private Runnable onPlay;
	private Runnable onPause;
	private Runnable onEnd;
	private Runnable onSeek;
	
	private AudioPlayerBuilder() {}
	
	public AudioPlayerBuilder onEnd(Runnable onEnd) {
		this.onEnd = onEnd;
		return this;
	}
	
	public AudioPlayerBuilder onPlay(Runnable onPlay) {
		this.onPlay = onPlay;
		return this;
	}
	
	public AudioPlayerBuilder onPause(Runnable onPause) {
		this.onPause = onPause;
		return this;
	}
	
	public AudioPlayerBuilder onSeek(Runnable onSeek) {
		this.onSeek = onSeek;
		return this;
	}
	
	public AudioPlayerBuilder withLibrary(RookitLibrary library) {
		this.library = library;
		return this;
	}
	
	public AudioPlayerBuilder bindVolumeTo(DoubleBinding binding) {
		this.volumeBinding = binding;
		return this;
	}
	
	public AudioPlayer build() {
		try {
			return new MasterPlayer(library, this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void apply(MediaPlayer player) {
		if(player != null) {
			applyOnPlay(player);
			applyOnPause(player);
			applyOnEnd(player);
			applyVolumeBinding(player);
		}
	}

	private void applyVolumeBinding(MediaPlayer player) {
		if(volumeBinding != null) {
			player.volumeProperty().bind(volumeBinding);
		}
	}

	private void applyOnEnd(MediaPlayer player) {
		if(onEnd != null) {
			player.setOnEndOfMedia(onEnd);
		}
	}

	private void applyOnPause(MediaPlayer player) {
		if(onPause != null) {
			player.setOnPaused(onPause);
		}
	}

	private void applyOnPlay(MediaPlayer player) {
		if(onPlay != null) {
			player.setOnPlaying(onPlay);
		}
	}

	public RookitLibrary getLibrary() {
		return library;
	}

	public DoubleBinding getVolumeBinding() {
		return volumeBinding;
	}

	public Runnable getOnPlay() {
		return onPlay;
	}

	public Runnable getOnPause() {
		return onPause;
	}

	public Runnable getOnEnd() {
		return onEnd;
	}

	public Runnable getOnSeek() {
		return onSeek;
	}
	
}
