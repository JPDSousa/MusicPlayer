package app.musicplayer.rookit;

import java.time.Duration;

import org.rookit.dm.album.Album;
import org.rookit.dm.track.Track;
import org.rookit.mongodb.DBManager;

import javafx.scene.image.Image;


@SuppressWarnings("javadoc")
public final class Utils {
	
	public static final String getDurationClockString(Track track) {
		final Duration duration = Duration.ofMillis(track.getDuration());
		final long seconds = duration.getSeconds() % 60;
		return duration.toMinutes() + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}

	public static Image getAlbumArtwork(DBManager library, Album album) {
		return new Image(library.stream(album.getCover()));
	}

}
