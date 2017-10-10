package app.musicplayer.rookit;

import java.io.InputStream;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.rookit.dm.track.Track;
import org.rookit.mongodb.DBManager;
import org.smof.gridfs.SmofGridRef;

import com.google.common.collect.Lists;

import app.musicplayer.model.Playlist;
import app.musicplayer.rookit.dm.MPAlbum;
import app.musicplayer.rookit.dm.MPArtist;
import app.musicplayer.rookit.dm.MPFactory;
import app.musicplayer.rookit.dm.MPGenre;
import app.musicplayer.rookit.dm.MPTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

@SuppressWarnings("javadoc")
public class RookitLibrary {
	
	private static RookitLibrary singleton;
	
	public static RookitLibrary create(DBManager database) {
		if(singleton == null) {
			singleton = new RookitLibrary(database);
		}
		return singleton;
	}
	
	private final DBManager database;
	private final MPFactory factory;

	private RookitLibrary(DBManager database) {
		super();
		database.init();
		this.database = database;
		factory = MPFactory.getDefault();
	}
	
	private <T> ObservableList<T> fromStream(Stream<T> stream) {
		return FXCollections.observableArrayList(stream.collect(Collectors.toList()));
	}
	
	public Stream<MPTrack> streamTracks() {
		return database.getTracks()
				.stream()
				.map(factory::fromTrack);
	}
	
	public ObservableList<MPTrack> getAllTracks() {
		return fromStream(streamTracks());
	}
	
	public Stream<MPAlbum> streamAlbums() {
		return database.getAlbums()
				.stream().map(album -> factory.fromAlbum(album, this));
	}
	
	public ObservableList<MPAlbum> getAllAlbums() {
		return fromStream(streamAlbums());
	}
		
	public Stream<MPArtist> streamArtists() {
		return database.getArtists()
				.stream().map(factory::fromArtist);
	}
	
	public ObservableList<MPAlbum> getArtistAlbums(MPArtist artist) {
		final Stream<MPAlbum> stream = database.getAlbums().withArtist(artist)
				.stream().map(album -> factory.fromAlbum(album, this));
		return fromStream(stream);
	}
	
	public ObservableList<MPArtist> getAllArtists() {
		return fromStream(streamArtists());
	}
	
	public MPArtist getArtist(String name) {
		return factory.fromArtist(database.getArtists().withName(name).first());
	}
	
	public Stream<MPGenre> streamGenres() {
		return database.getGenres()
				.stream().map(factory::fromGenre);
	}
	
	public ObservableList<MPGenre> getAllGenres() {
		return fromStream(streamGenres());
	}

	public InputStream stream(SmofGridRef ref) {
		return database.stream(ref);
	}

	public ObservableList<MPTrack> fromTracks(Collection<Track> tracks) {
		return fromStream(tracks.stream().map(factory::fromTrack));
	}
	
	public ObservableList<MPTrack> fromTracks(Iterable<Track> tracks) {
		return fromStream(StreamSupport.stream(tracks.spliterator(), false).map(factory::fromTrack));
	}

	public ObservableList<Playlist> getAllPlaylists() {
		return FXCollections.observableArrayList();
	}
	
	public Playlist getPlaylist(String title) {
		return new Playlist(0, title, Lists.newArrayList());
	}

	public DBManager getDatabase() {
		return database;
	}

	public MPFactory getFactory() {
		return factory;
	}
	

}
