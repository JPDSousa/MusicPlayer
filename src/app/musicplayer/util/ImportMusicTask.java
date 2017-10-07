package app.musicplayer.util;

import javafx.concurrent.Task;

public abstract class ImportMusicTask<V> extends Task<V> {
	
	public void updateProgress(long progress, long maxProgress) {
		updateProgress(progress, maxProgress);
	}
}
