package app.musicplayer.rookit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections4.list.SetUniqueList;

import com.google.common.collect.Lists;

import app.musicplayer.rookit.dm.MPTrack;

@SuppressWarnings("javadoc")
public class CurrentPlaylist implements Iterator<MPTrack> {
	
	private final List<MPTrack> playlist;
	private final RookitLibrary library;
	private int index;
	private boolean isLoopActive;
	private boolean isShuffleActive;
	
	public CurrentPlaylist(RookitLibrary library) {
		playlist = SetUniqueList.setUniqueList(Lists.newArrayList());
		this.library = library;
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
	
	public String getCurrentURI() {
		return downloadToFile(library, getCurrent()).toUri().toString();
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
	
	public String skipTo(MPTrack track) {
		final int index = playlist.indexOf(track);
		if (index > 0) {
            this.index = index;
            return getCurrentURI();
        }
		return null;
	}

	private Path downloadToFile(RookitLibrary library, MPTrack track) {
		final Path path;
    	if(track.getPath() != null) {
    		try {
    			path = Paths.get("data", track.getIdAsString() + ".mp3");
    			if(!Files.exists(path)) {
        	    	Files.copy(library.stream(track.getPath()), path);
    			}
    	    	return path;
    		} catch (IOException e) {
    			throw new RuntimeException(e);
    		}
    	}
    	return null;
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

	public void markCurrentAsPlayed(int secondsPlayed) {
		final MPTrack current = getCurrent();
		long length = current.getDuration();
		if ((100 * secondsPlayed / length) > 50) {
			current.play();
		}
		current.setPlaying(false);
	}
}
