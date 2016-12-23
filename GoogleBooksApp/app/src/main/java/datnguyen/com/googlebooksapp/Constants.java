package datnguyen.com.googlebooksapp;

/**
 * Created by datnguyen on 12/22/16.
 */

public class Constants {

	public final static String URL_BOOKS_BASE = "https://www.googleapis.com/books";

	public final static String URL_BOOKS_SEARCH = "/v1/volumes?q=%s&maxResults=%d&startIndex=%d";
	public final static int REQUEST_ITEMS_PER_PAGE = 10;

	public final static int CONNECTION_TIMEOUT = 30000; //miliseconds

}
