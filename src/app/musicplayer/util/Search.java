package app.musicplayer.util;

import app.musicplayer.model.SearchResult;
import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.dm.MPFactory;

import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@SuppressWarnings("javadoc")
public class Search implements Runnable{
	
	private final BooleanProperty hasResults;
	private SearchResult result;
	private Thread searchThread;
	private final RookitLibrary library;
	private final MPFactory factory;
	private String text;

	public Search(RookitLibrary library) {
		this.library = library;
		this.factory = library.getFactory();
		hasResults = new SimpleBooleanProperty(false);
	}

	public BooleanProperty hasResultsProperty() { 
		return hasResults; 
	}

	public SearchResult getResult() {
		hasResults.set(false);
		return result;
	}

	public synchronized void search(String searchText) {
		if (searchThread != null && searchThread.isAlive()) {
			searchThread.interrupt();
		}
		text = searchText.toUpperCase();
		searchThread = new Thread(this);
		searchThread.start();
	}

	@Override
	public void run() {
		hasResults.set(false);
		final Pattern regex = Pattern.compile(".*"+Pattern.quote(text), Pattern.CASE_INSENSITIVE);
		final SearchResult result = new SearchResult();
		library.getDatabase().getTracks().withTitle(regex).stream().limit(3).map(factory::fromTrack).forEach(result::addResult);
		library.getDatabase().getAlbums().withTitle(regex).stream().limit(3).map(album -> factory.fromAlbum(album, library)).forEach(result::addResult);
		library.getDatabase().getArtists().withName(regex).stream().limit(3).map(artist -> factory.fromArtist(artist, library)).forEach(result::addResult);
		library.getDatabase().getGenres().withName(regex).stream().limit(3).map(factory::fromGenre).forEach(result::addResult);
		this.result = result;
		hasResults.set(true);
	}
}