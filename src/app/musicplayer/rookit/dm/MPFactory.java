package app.musicplayer.rookit.dm;

import org.rookit.dm.album.Album;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.genre.Genre;
import org.rookit.dm.track.Track;

import app.musicplayer.rookit.RookitLibrary;

@SuppressWarnings("javadoc")
public class MPFactory {
	
	private static MPFactory singleton;
	
	public static MPFactory getDefault() {
		if(singleton == null) {
			singleton = new MPFactory();
		}
		return singleton;
	}
	
	private MPFactory() {}
	
	public MPTrack fromTrack(Track track) {
		return new MPTrackImpl(track);
	}
	
	public MPAlbum fromAlbum(Album album, RookitLibrary library) {
		return new MPAlbumImpl(album, library);
	}
	
	public MPArtist fromArtist(Artist artist) {
		return new MPArtistImpl(artist);
	}
	
	public MPGenre fromGenre(Genre genre) {
		return new MPGenreImpl(genre);
	}

}
