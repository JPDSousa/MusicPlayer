package app.musicplayer.rookit.dm;

import org.rookit.dm.album.Album;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

@SuppressWarnings("javadoc")
public interface MPAlbum extends Album {

	public String getArtistsAsString();
	
	public SimpleObjectProperty<Image> artworkProperty();
	
	public Iterable<MPTrack> getMPTracks();
}
