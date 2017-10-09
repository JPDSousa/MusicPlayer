package app.musicplayer.model;

import java.util.List;

import org.rookit.dm.track.Track;

import com.google.common.collect.Lists;

import org.rookit.dm.album.Album;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.genre.Genre;

@SuppressWarnings("javadoc")
public class SearchResult {

    private final List<Track> songResults;
    private final List<Album> albumResults;
    private final List<Artist> artistResults;
    private final List<Genre> genreResults;

    public SearchResult() {
        this.songResults = Lists.newArrayList();
        this.albumResults = Lists.newArrayList();
        this.artistResults = Lists.newArrayList();
        this.genreResults = Lists.newArrayList();
    }
    
    public void addResult(Track track) {
    	songResults.add(track);
    }
    
	public List<Track> getSongResults() {
		return songResults;
	}
	
	public void addResult(Album album) {
		albumResults.add(album);
	}

	public List<Album> getAlbumResults() {
		return albumResults;
	}
	
	public void addResult(Artist artist) {
		artistResults.add(artist);
	}

	public List<Artist> getArtistResults() {
		return artistResults;
	}
	
	public void addResult(Genre genre) {
		genreResults.add(genre);
	}

	public List<Genre> getGenreResults() {
		return genreResults;
	}

}
