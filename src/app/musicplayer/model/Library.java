package app.musicplayer.model;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.rookit.dm.track.Track;
import org.rookit.mongodb.DBManager;

import com.google.common.collect.Lists;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("javadoc")
final class Library {

	private static Library library;
	
	public static Library getDefault() {
		if(library == null) {
			library = new Library();
		}
		return library;
	}
	
	private final DBManager database;
	
	private Library() {
		database = DBManager.open("localhost", 27039, "rookit");
		database.init();
	}
	
    public boolean isSupportedFileType(String fileName) {

        String extension = "";
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1).toLowerCase();
        }
        switch (extension) {
            // MP3
            case "mp3":
                // MP4
            case "mp4":
            case "m4a":
            case "m4v":
                // WAV
            case "wav":
                return true;
            default:
                return false;
        }
    }

    /**
     * Gets a list of songs.
     * @return observable list of songs
     */
    public ObservableList<Song> getSongs() {
        return FXCollections.observableArrayList(
        		database.getTracks().stream()
        		.map(this::fromTrack)
        		.collect(Collectors.toList()));
    }

    public Song getSong(String title) {
        return fromTrack(database.getTracks().withTitle(title).first());
    }
    
    private Song fromTrack(Track track) {
    	final Song song =  new Song(track.getId().hashCode(), track.getTitle().toString(), 
    			track.getMainArtists().toString(), 
    			"album", Duration.ofMillis(track.getDuration()), 1, 1, (int) track.getPlays(), 
    			null, track.getPath(), database);
    	return song;
    }

    /**
     * Gets a list of albums.
     *
     * @return observable list of albums
     */
    public ObservableList<Album> getAlbums() {
        return FXCollections.observableArrayList(database.getAlbums()
        		.stream().map(this::fromAlbum)
        		.collect(Collectors.toList()));
    }
    
    private Album fromAlbum(org.rookit.dm.album.Album album) {
    	if(album == null) {
    		return null;
    	}
    	return new Album(album.getId().hashCode(), album.getTitle(), 
    			album.getArtists().toString(),
    			StreamSupport.stream(album.getTracks().spliterator(), false)
    			.map(this::fromTrack)
    			.collect(Collectors.toList()));
    }

    public Album getAlbum(String title) {
        return fromAlbum(database.getAlbums().withTitle(title).first());
    }

    private Artist fromArtist(org.rookit.dm.artist.Artist artist) {
    	if(artist == null) {
    		return null;
    	}
    	return new Artist(artist.getName(), Lists.newArrayList());
    }
    
    /**
     * Gets a list of artists.
     *
     * @return observable list of artists
     */
    public ObservableList<Artist> getArtists() {
        return FXCollections.observableArrayList(database.getArtists().stream()
        		.map(this::fromArtist)
        		.collect(Collectors.toList()));
    }

    public Artist getArtist(String title) {
        return fromArtist(database.getArtists().withName(title).first());
    }

	public ObservableList<Playlist> getPlaylists() {
		return FXCollections.observableArrayList();
	}

	public Playlist getPlaylist(String text) {
		return new Playlist(new Random().nextInt(), text, Lists.newArrayList());
	}

	public void addPlaylist(String text) {
		// TODO Auto-generated method stub
		
	}

	public Playlist getPlaylist(int selectedPlayListId) {
		return new Playlist(selectedPlayListId, "randomTitle", Lists.newArrayList());
	}
}
