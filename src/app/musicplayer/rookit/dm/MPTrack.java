package app.musicplayer.rookit.dm;

import org.rookit.dm.track.Track;

import javafx.beans.property.SimpleBooleanProperty;


@SuppressWarnings("javadoc")
public interface MPTrack extends Track {
	
	SimpleBooleanProperty playingProperty();
	boolean isPlaying();
	void setPlaying(boolean playing);
	
	SimpleBooleanProperty selectedProperty();
	void setSelected(boolean selected);
	boolean isSelected();
	

}
