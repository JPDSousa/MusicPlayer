package app.musicplayer.rookit.dm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.bson.types.ObjectId;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.artist.TypeArtist;
import org.rookit.dm.genre.Genre;
import org.smof.gridfs.SmofGridRef;

import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.Utils;
import app.musicplayer.util.Resources;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

@SuppressWarnings("javadoc")
public class MPArtistImpl implements MPArtist {

	private final Artist delegate;
	private final SimpleObjectProperty<Image> artworkProperty;

	MPArtistImpl(Artist delegate, RookitLibrary library) {
		super();
		this.delegate = delegate;
		this.artworkProperty = new SimpleObjectProperty<>(loadArtwork(delegate, library));
	}

	private Image loadArtwork(Artist delegate, RookitLibrary library) {
		if(delegate.getPicture() != null && delegate.getPicture().getId() != null) {
			return Utils.getArtistArtwork(library, delegate);
		}
		return new Image(Resources.IMG + "artistsIcon.png");
	}

	@Override
	public void addAlias(String arg0) {
		delegate.addAlias(arg0);
	}

	@Override
	public void addGenre(Genre arg0) {
		delegate.addGenre(arg0);
	}

	@Override
	public void addRelatedArtist(Artist arg0) {
		delegate.addRelatedArtist(arg0);
	}

	@Override
	public int compareTo(Artist o) {
		return delegate.compareTo(o);
	}

	@Override
	public Iterable<String> getAliases() {
		return delegate.getAliases();
	}

	@Override
	public Iterable<Genre> getAllGenres() {
		return delegate.getAllGenres();
	}

	@Override
	public LocalDate getBeginDate() {
		return delegate.getBeginDate();
	}

	@Override
	public long getDuration() {
		return delegate.getDuration();
	}

	@Override
	public LocalDate getEndDate() {
		return delegate.getEndDate();
	}

	@Override
	public Iterable<Genre> getGenres() {
		return delegate.getGenres();
	}

	@Override
	public String getIPI() {
		return delegate.getIPI();
	}

	@Override
	public String getISNI() {
		return delegate.getISNI();
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
	public String getName() {
		return delegate.getName();
	}

	@Override
	public String getOrigin() {
		return delegate.getOrigin();
	}

	@Override
	public long getPlays() {
		return delegate.getPlays();
	}

	@Override
	public Iterable<Artist> getRelatedArtists() {
		return delegate.getRelatedArtists();
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
	public TypeArtist getType() {
		return delegate.getType();
	}

	@Override
	public void play() {
		delegate.play();
	}

	@Override
	public void setAliases(Set<String> arg0) {
		delegate.setAliases(arg0);
	}

	@Override
	public void setBeginDate(LocalDate arg0) {
		delegate.setBeginDate(arg0);
	}

	@Override
	public void setDuration(long arg0) {
		delegate.setDuration(arg0);
	}

	@Override
	public void setEndDate(LocalDate arg0) {
		delegate.setEndDate(arg0);
	}

	@Override
	public void setGenres(Set<Genre> arg0) {
		delegate.setGenres(arg0);
	}

	@Override
	public void setIPI(String arg0) {
		delegate.setIPI(arg0);
	}

	@Override
	public void setISNI(String arg0) {
		delegate.setISNI(arg0);
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
	public void setOrigin(String arg0) {
		delegate.setOrigin(arg0);
	}

	@Override
	public void setPlays(long arg0) {
		delegate.setPlays(arg0);
	}

	@Override
	public void setSkipped(long arg0) {
		delegate.setSkipped(arg0);
	}

	@Override
	public void skip() {
		delegate.skip();
	}

	@Override
	public SimpleObjectProperty<Image> imageProperty() {
		return artworkProperty;
	}

	@Override
	public Image getImage() {
		return artworkProperty.get();
	}

	@Override
	public SmofGridRef getPicture() {
		return delegate.getPicture();
	}

	@Override
	public void setPicture(byte[] arg0) {
		delegate.setPicture(arg0);
	}


}
