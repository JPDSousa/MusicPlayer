package app.musicplayer.rookit.dm;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.bson.types.ObjectId;
import org.rookit.dm.genre.Genre;

@SuppressWarnings("javadoc")
public class MPGenreImpl implements MPGenre {

	private final Genre delegate;

	public MPGenreImpl(Genre delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	public int compareTo(Genre o) {
		return delegate.compareTo(o);
	}

	@Override
	public String getDescription() {
		return delegate.getDescription();
	}

	@Override
	public long getDuration() {
		return delegate.getDuration();
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
	public long getPlays() {
		return delegate.getPlays();
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
	public void play() {
		delegate.play();
	}

	@Override
	public void setDescription(String arg0) {
		delegate.setDescription(arg0);
	}

	@Override
	public void setDuration(long arg0) {
		delegate.setDuration(arg0);
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
	public void setSkipped(long arg0) {
		delegate.setSkipped(arg0);
	}

	@Override
	public void skip() {
		delegate.skip();
	}
	
	
}
