package app.musicplayer.rookit.dm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.bson.types.ObjectId;
import org.rookit.dm.album.Album;
import org.rookit.dm.album.TypeAlbum;
import org.rookit.dm.album.TypeRelease;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.genre.Genre;
import org.rookit.dm.track.Track;
import org.rookit.dm.utils.PrintUtils;
import org.smof.gridfs.SmofGridRef;

import com.google.common.collect.Lists;

import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.Utils;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

@SuppressWarnings("javadoc")
public class MPAlbumImpl implements MPAlbum {

	private final Album delegate;
	private final SimpleObjectProperty<Image> artworkProperty;
	private final List<MPTrack> mpTracks;
	private final MPFactory factory;

	MPAlbumImpl(Album delegate, RookitLibrary library) {
		super();
		factory = library.getFactory();
		this.delegate = delegate;
		this.artworkProperty = new SimpleObjectProperty<Image>(Utils.getAlbumArtwork(library, delegate));
		mpTracks = Lists.newArrayList();
		getTracks().forEach(track -> mpTracks.add(factory.fromTrack(track)));
	}

	@Override
	public void addArtist(Artist arg0) {
		delegate.addArtist(arg0);
	}

	@Override
	public void addGenre(Genre arg0) {
		delegate.addGenre(arg0);
	}

	@Override
	public void addTrack(Track arg0, Integer arg1, String arg2) {
		delegate.addTrack(arg0, arg1, arg2);
		mpTracks.add(factory.fromTrack(arg0));
	}

	@Override
	public void addTrack(Track arg0, Integer arg1) {
		delegate.addTrack(arg0, arg1);
		mpTracks.add(factory.fromTrack(arg0));
	}

	@Override
	public void addTrackLast(Track arg0, String arg1) {
		delegate.addTrackLast(arg0, arg1);
		mpTracks.add(factory.fromTrack(arg0));
	}

	@Override
	public void addTrackLast(Track arg0) {
		delegate.addTrackLast(arg0);
		mpTracks.add(factory.fromTrack(arg0));
	}

	@Override
	public int compareTo(Album o) {
		return delegate.compareTo(o);
	}

	@Override
	public boolean contains(String arg0, Integer arg1) {
		return delegate.contains(arg0, arg1);
	}

	@Override
	public boolean contains(Track arg0) {
		return delegate.contains(arg0);
	}

	@Override
	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	@Override
	public TypeAlbum getAlbumType() {
		return delegate.getAlbumType();
	}

	@Override
	public Iterable<Genre> getAllGenres() {
		return delegate.getAllGenres();
	}

	@Override
	public Iterable<Artist> getArtists() {
		return delegate.getArtists();
	}

	@Override
	public SmofGridRef getCover() {
		return delegate.getCover();
	}

	@Override
	public int getDiscCount() {
		return delegate.getDiscCount();
	}

	@Override
	public Iterable<String> getDiscs() {
		return delegate.getDiscs();
	}

	@Override
	public long getDuration() {
		return delegate.getDuration();
	}

	@Override
	public double getDurationSec() {
		return delegate.getDurationSec();
	}

	@Override
	public double getDurationSec(String arg0) {
		return delegate.getDurationSec(arg0);
	}

	@Override
	public String getFullTitle() {
		return delegate.getFullTitle();
	}

	@Override
	public Iterable<Genre> getGenres() {
		return delegate.getGenres();
	}

	@Override
	public ObjectId getId() {
		return delegate.getId();
	}

	@Override
	public String getIdAsString() {
		return delegate.getIdAsString();
	}

	@Override
	public LocalDate getLastPlayed() {
		return delegate.getLastPlayed();
	}

	@Override
	public LocalDate getLastSkipped() {
		return delegate.getLastSkipped();
	}

	@Override
	public long getPlays() {
		return delegate.getPlays();
	}

	@Override
	public LocalDate getReleaseDate() {
		return delegate.getReleaseDate();
	}

	@Override
	public TypeRelease getReleaseType() {
		return delegate.getReleaseType();
	}

	@Override
	public long getSkipped() {
		return delegate.getSkipped();
	}

	@Override
	public LocalDateTime getStorageTime() {
		return delegate.getStorageTime();
	}

	@Override
	public String getTitle() {
		return delegate.getTitle();
	}

	@Override
	public Track getTrack(String arg0, Integer arg1) {
		return delegate.getTrack(arg0, arg1);
	}

	@Override
	public String getTrackDisc(Track arg0) {
		return delegate.getTrackDisc(arg0);
	}

	@Override
	public Integer getTrackNumber(Track arg0) {
		return delegate.getTrackNumber(arg0);
	}

	@Override
	public Iterable<Integer> getTrackNumbers(String arg0) {
		return delegate.getTrackNumbers(arg0);
	}

	@Override
	public Iterable<Track> getTracks() {
		return delegate.getTracks();
	}

	@Override
	public Iterable<Track> getTracks(String arg0) {
		return delegate.getTracks(arg0);
	}

	@Override
	public int getTracksCount() {
		return delegate.getTracksCount();
	}

	@Override
	public int getTracksCount(String arg0) {
		return delegate.getTracksCount(arg0);
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public void play() {
		delegate.play();
	}

	@Override
	public void relocate(String arg0, Integer arg1, String arg2, Integer arg3) {
		delegate.relocate(arg0, arg1, arg2, arg3);
	}

	@Override
	public void setArtists(Set<Artist> arg0) {
		delegate.setArtists(arg0);
	}

	@Override
	public void setCover(byte[] arg0) {
		delegate.setCover(arg0);
	}

	@Override
	public void setDuration(long arg0) {
		delegate.setDuration(arg0);
	}

	@Override
	public void setGenres(Set<Genre> arg0) {
		delegate.setGenres(arg0);
	}

	@Override
	public void setId(ObjectId arg0) {
		delegate.setId(arg0);
	}

	@Override
	public void setLastPlayed(LocalDate arg0) {
		delegate.setLastPlayed(arg0);
	}

	@Override
	public void setLastSkipped(LocalDate arg0) {
		delegate.setLastSkipped(arg0);
	}

	@Override
	public void setPlays(long arg0) {
		delegate.setPlays(arg0);
	}

	@Override
	public void setReleaseDate(LocalDate arg0) {
		delegate.setReleaseDate(arg0);
	}

	@Override
	public void setSkipped(long arg0) {
		delegate.setSkipped(arg0);
	}

	@Override
	public void setTitle(String arg0) {
		delegate.setTitle(arg0);
	}

	@Override
	public void skip() {
		delegate.skip();
	}

	@Override
	public String getArtistsAsString() {
		return PrintUtils.getIterableAsString(getArtists(), ", ");
	}

	@Override
	public SimpleObjectProperty<Image> artworkProperty() {
		return artworkProperty;
	}

	@Override
	public Iterable<MPTrack> getMPTracks() {
		return mpTracks;
	}
	
	
}
