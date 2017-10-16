package app.musicplayer.rookit.dm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import org.bson.types.ObjectId;
import org.rookit.dm.artist.Artist;
import org.rookit.dm.genre.Genre;
import org.rookit.dm.track.Track;
import org.rookit.dm.track.TrackTitle;
import org.rookit.dm.track.TypeTrack;
import org.rookit.dm.track.VersionTrack;
import org.rookit.dm.utils.PrintUtils;
import org.rookit.utils.print.TypeFormat;
import org.smof.gridfs.SmofGridRef;

import app.musicplayer.rookit.Utils;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Duration;

@SuppressWarnings("javadoc")
public class MPTrackImpl implements MPTrack {

	private final Track delegate;
	private final BooleanProperty playingProperty;
	private final BooleanProperty selectedProperty;
	private final StringProperty titleProperty;
	private final StringProperty artistProperty;
	private final StringProperty lengthProperty;
	private final LongProperty playsProperty;

	MPTrackImpl(Track delegate) {
		super();
		this.delegate = delegate;
		this.playingProperty = new SimpleBooleanProperty(false);
		this.selectedProperty = new SimpleBooleanProperty(false);
		this.titleProperty = new SimpleStringProperty(getTitle().toString());
		this.artistProperty = new SimpleStringProperty(getArtistsAsString());
		this.lengthProperty = new SimpleStringProperty(Utils.duration2ClockString(new Duration(getDuration())));
		this.playsProperty = new SimpleLongProperty(getPlays());
	}
	
	private String getArtistsAsString() {
		return PrintUtils.getIterableAsString(getMainArtists(), TypeFormat.TITLE);
	}

	@Override
	public void addFeature(Artist arg0) {
		delegate.addFeature(arg0);
	}

	@Override
	public void addGenre(Genre arg0) {
		delegate.addGenre(arg0);
	}

	@Override
	public void addMainArtist(Artist arg0) {
		delegate.addMainArtist(arg0);
		artistProperty.setValue(getArtistsAsString());
	}

	@Override
	public void addProducer(Artist arg0) {
		delegate.addProducer(arg0);
	}

	@Override
	public int compareTo(Track o) {
		return delegate.compareTo(o);
	}

	@Override
	public boolean equals(Object arg0) {
		return delegate.equals(arg0);
	}

	@Override
	public Iterable<Genre> getAllGenres() {
		return delegate.getAllGenres();
	}

	@Override
	public VersionTrack getAsVersionTrack() {
		return delegate.getAsVersionTrack();
	}

	@Override
	public short getBPM() {
		return delegate.getBPM();
	}

	@Override
	public long getDuration() {
		return delegate.getDuration();
	}

	@Override
	public Iterable<Artist> getFeatures() {
		return delegate.getFeatures();
	}

	@Override
	public TrackTitle getFullTitle() {
		return delegate.getFullTitle();
	}

	@Override
	public Iterable<Genre> getGenres() {
		return delegate.getGenres();
	}

	@Override
	public String getHiddenTrack() {
		return delegate.getHiddenTrack();
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
	public TrackTitle getLongFullTitle() {
		return delegate.getLongFullTitle();
	}

	@Override
	public String getLyrics() {
		return delegate.getLyrics();
	}

	@Override
	public Iterable<Artist> getMainArtists() {
		return delegate.getMainArtists();
	}

	@Override
	public SmofGridRef getPath() {
		return delegate.getPath();
	}

	@Override
	public long getPlays() {
		return delegate.getPlays();
	}

	@Override
	public Iterable<Artist> getProducers() {
		return delegate.getProducers();
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
	public TrackTitle getTitle() {
		return delegate.getTitle();
	}

	@Override
	public TypeTrack getType() {
		return delegate.getType();
	}

	@Override
	public int hashCode() {
		return delegate.hashCode();
	}

	@Override
	public boolean isExplicit() {
		return delegate.isExplicit();
	}

	@Override
	public boolean isVersionTrack() {
		return delegate.isVersionTrack();
	}

	@Override
	public void play() {
		delegate.play();
		playsProperty.setValue(getPlays());
	}

	@Override
	public void setBPM(short arg0) {
		delegate.setBPM(arg0);
	}

	@Override
	public void setDuration(long arg0) {
		delegate.setDuration(arg0);
	}

	@Override
	public void setExplicit(boolean arg0) {
		delegate.setExplicit(arg0);
	}

	@Override
	public void setFeatures(Set<Artist> arg0) {
		delegate.setFeatures(arg0);
	}

	@Override
	public void setGenres(Set<Genre> arg0) {
		delegate.setGenres(arg0);
	}

	@Override
	public void setHiddenTrack(String arg0) {
		delegate.setHiddenTrack(arg0);
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
	public void setLyrics(String arg0) {
		delegate.setLyrics(arg0);
	}

	@Override
	public void setMainArtists(Set<Artist> arg0) {
		delegate.setMainArtists(arg0);
	}

	@Override
	public void setPlays(long arg0) {
		delegate.setPlays(arg0);
		playsProperty.setValue(getPlays());
	}

	@Override
	public void setProducers(Set<Artist> arg0) {
		delegate.setProducers(arg0);
	}

	@Override
	public void setSkipped(long arg0) {
		delegate.setSkipped(arg0);
	}

	@Override
	public void setTitle(String arg0) {
		delegate.setTitle(arg0);
		titleProperty.setValue(getTitle().toString());
	}

	@Override
	public void setTitle(TrackTitle arg0) {
		delegate.setTitle(arg0);
		titleProperty.setValue(getTitle().toString());
	}

	@Override
	public void skip() {
		delegate.skip();
	}

	@Override
	public BooleanProperty playingProperty() {
		return playingProperty;
	}

	@Override
	public boolean isPlaying() {
		return playingProperty.get();
	}

	@Override
	public BooleanProperty selectedProperty() {
		return selectedProperty;
	}

	@Override
	public void setSelected(boolean selected) {
		selectedProperty.set(selected);
	}

	@Override
	public boolean isSelected() {
		return selectedProperty.get();
	}
	
	@Override
	public void setPlaying(boolean playing) {
		playingProperty.set(playing);
	}

	@Override
	public StringProperty titleProperty() {
		return titleProperty;
	}

	@Override
	public StringProperty artistsProperty() {
		return artistProperty;
	}

	@Override
	public StringProperty lenghProperty() {
		return lengthProperty;
	}

	@Override
	public LongProperty playsProperty() {
		return playsProperty;
	}
	

}
