package app.musicplayer.util;

import app.musicplayer.model.SearchResult;

import java.util.regex.Pattern;

import org.rookit.mongodb.DBManager;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@SuppressWarnings("javadoc")
public class Search implements Runnable{
	
	private final BooleanProperty hasResults;
	private SearchResult result;
	private Thread searchThread;
	private final DBManager database;
	private String text;

	public Search(DBManager database) {
		this.database = database;
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
		database.getTracks().withTitle(regex).stream().limit(3).forEach(result::addResult);
		database.getAlbums().withTitle(regex).stream().limit(3).forEach(result::addResult);
		database.getArtists().withName(regex).stream().limit(3).forEach(result::addResult);
		database.getGenres().withName(regex).stream().limit(3).forEach(result::addResult);
		this.result = result;
		hasResults.set(true);
	}
}