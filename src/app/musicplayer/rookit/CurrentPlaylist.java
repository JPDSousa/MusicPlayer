package app.musicplayer.rookit;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.list.SetUniqueList;

import com.google.common.collect.Lists;

import app.musicplayer.rookit.dm.MPTrack;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class CurrentPlaylist implements Iterator<MPTrack> {
	
	private final List<MPTrack> playlist;
	private int index;
	private boolean isLoopActive;
	private boolean isShuffleActive;
	
	public CurrentPlaylist() {
		playlist = SetUniqueList.setUniqueList(Lists.newArrayList());
		index = 0;
		isLoopActive = false;
		isShuffleActive = false;
	}
	
	public Iterable<MPTrack> getPlaylist() {
		return playlist;
	}

	public boolean isShuffleActive() {
		return isShuffleActive;
	}

	public void setShuffleActive(boolean isShuffleActive) {
		this.isShuffleActive = isShuffleActive;
	}

	public boolean isLoopActive() {
		return isLoopActive;
	}

	public void setLoopActive(boolean isLoopActive) {
		this.isLoopActive = isLoopActive;
	}

	public MPTrack getCurrent() {
		return playlist.get(index);
	}
	
	public void add(MPTrack track) {
		playlist.add(track);
	}
	
	public void addAll(Iterable<MPTrack> tracks) {
		tracks.forEach(playlist::add);
	}
	
	public boolean contains(MPTrack track) {
		return playlist.contains(track);
	}
	
	public void skipTo(MPTrack track) {
		final int index = playlist.indexOf(track);
		if (index > 0) {
            this.index = index;
        }
	}
	
	public void clear() {
		playlist.clear();
		resetIndex();
	}
	
	private void resetIndex() {
		index = 0;
	}

	@Override
	public boolean hasNext() {
		return (isLoopActive && !playlist.isEmpty()) || index < playlist.size();
	}

	@Override
	public MPTrack next() {
		if(isShuffleActive) {
			final Random random = new Random();
			index = random.nextInt(playlist.size());
			return getCurrent();
		}
		if(isLoopActive && index == playlist.size()) {
			index = 0;
			return getCurrent();
		}
		return playlist.get(++index);
	}
	
	public boolean hasPrevious() {
		return index > 0;
	}
	
	public MPTrack previous() {
		return playlist.get(--index);
	}

	public void markCurrentAsPlayed(Duration durationPlayed) {
		final MPTrack current = getCurrent();
		long length = current.getDuration();
		if ((100 * durationPlayed.toSeconds() / length) > 50) {
			current.play();
		}
		current.setPlaying(false);
	}
}
