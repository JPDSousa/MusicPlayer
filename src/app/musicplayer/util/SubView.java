package app.musicplayer.util;

import org.rookit.dm.track.Track;

@SuppressWarnings("javadoc")
public interface SubView {

	void scroll(char letter);
	void play();
	Track getSelectedSong();
}
