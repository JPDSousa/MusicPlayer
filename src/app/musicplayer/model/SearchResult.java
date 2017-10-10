package app.musicplayer.model;

import java.util.List;

import com.google.common.collect.Lists;

import app.musicplayer.rookit.dm.MPAlbum;
import app.musicplayer.rookit.dm.MPArtist;
import app.musicplayer.rookit.dm.MPGenre;
import app.musicplayer.rookit.dm.MPTrack;

@SuppressWarnings("javadoc")
public class SearchResult {

    private final List<MPTrack> songResults;
    private final List<MPAlbum> albumResults;
    private final List<MPArtist> artistResults;
    private final List<MPGenre> genreResults;

    public SearchResult() {
        this.songResults = Lists.newArrayList();
        this.albumResults = Lists.newArrayList();
        this.artistResults = Lists.newArrayList();
        this.genreResults = Lists.newArrayList();
    }
    
    public void addResult(MPTrack track) {
    	songResults.add(track);
    }
    
	public List<MPTrack> getSongResults() {
		return songResults;
	}
	
	public void addResult(MPAlbum album) {
		albumResults.add(album);
	}

	public List<MPAlbum> getAlbumResults() {
		return albumResults;
	}
	
	public void addResult(MPArtist artist) {
		artistResults.add(artist);
	}

	public List<MPArtist> getArtistResults() {
		return artistResults;
	}
	
	public void addResult(MPGenre genre) {
		genreResults.add(genre);
	}

	public List<MPGenre> getGenreResults() {
		return genreResults;
	}

}
