package app.musicplayer.rookit;

import org.rookit.dm.album.Album;
import org.rookit.dm.track.Track;

import javafx.scene.image.Image;
import javafx.util.Duration;


@SuppressWarnings("javadoc")
public final class Utils {
	
	public static final String getDurationClockString(Track track) {
		return duration2ClockString(new Duration(track.getDuration()));
	}

	public static Image getAlbumArtwork(RookitLibrary library, Album album) {
		return new Image(library.stream(album.getCover()));
	}
	
	public static final String duration2ClockString(Duration duration) {
		final long seconds = Math.round(duration.toSeconds()) % 60;
		return Math.round(duration.toMinutes()) + ":" + (seconds < 10 ? "0" + seconds : seconds);
	}

}
