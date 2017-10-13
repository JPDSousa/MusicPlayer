package app.musicplayer.audio;

import java.io.Closeable;

import app.musicplayer.rookit.dm.MPTrack;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public interface AudioPlayer extends Closeable {

	public void load(MPTrack track);
	
	public void play();

	public void pause();
	
	public void seek(Duration duration);
	
	public boolean isPlaying();
	
	public void mute(boolean mute);
	
	public Duration getCurrentTime();
	
	public Duration getRemainingTime();

	void stop();
}
