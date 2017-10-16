package app.musicplayer.rookit.dm;

import org.rookit.dm.track.Track;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;


@SuppressWarnings("javadoc")
public interface MPTrack extends Track {
	
	BooleanProperty playingProperty();
	boolean isPlaying();
	void setPlaying(boolean playing);
	
	BooleanProperty selectedProperty();
	void setSelected(boolean selected);
	boolean isSelected();
	
	StringProperty titleProperty();
	
	StringProperty artistsProperty();
	
	StringProperty lenghProperty();
	
	LongProperty playsProperty();
}
