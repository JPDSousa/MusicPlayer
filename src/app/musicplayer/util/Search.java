package app.musicplayer.util;

import app.musicplayer.model.SearchResult;
import app.musicplayer.rookit.RookitLibrary;
import app.musicplayer.rookit.dm.MPFactory;

import java.util.regex.Pattern;

import org.rookit.mongodb.DBManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@SuppressWarnings("javadoc")
public class Search implements Runnable{
	
	private final BooleanProperty hasResults;
	private SearchResult result;
	private Thread searchThread;
	private final DBManager library;
	private final MPFactory factory;
	private String text;

	public Search(RookitLibrary library) {
		this.library = library.getDatabase();
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
		library.getTracks().withTitle(regex).stream().limit(3).map(factory::fromTrack).forEach(result::addResult);
		library.getAlbums().withTitle(regex).stream().limit(3).map(factory::fromAlbum).forEach(result::addResult);
		library.getArtists().withName(regex).stream().limit(3).map(factory::fromArtist).forEach(result::addResult);
		library.getGenres().withName(regex).stream().limit(3).map(factory::fromGenre).forEach(result::addResult);
		this.result = result;
		hasResults.set(true);
	}
}