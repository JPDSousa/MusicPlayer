package app.musicplayer.rookit.dm;

import org.rookit.dm.artist.Artist;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

@SuppressWarnings("javadoc")
public interface MPArtist extends Artist {
	
	SimpleObjectProperty<Image> imageProperty();
	
	Image getImage();

}
