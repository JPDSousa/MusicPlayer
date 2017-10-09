package app.musicplayer.util;

import app.musicplayer.rookit.dm.MPTrack;

@SuppressWarnings("javadoc")
public interface SubView {

	void scroll(char letter);
	void play();
	MPTrack getSelectedSong();
}
