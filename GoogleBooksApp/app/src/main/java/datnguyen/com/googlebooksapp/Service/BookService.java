package datnguyen.com.googlebooksapp.Service;

/**
 * Created by datnguyen on 12/22/16.
 */

public final class BookService {

	private static BookService sharedInstance;

	public static BookService getBookService() {

		synchronized (BookService.class) {
			if (sharedInstance == null) {
				sharedInstance = new BookService();
			}
		}

		return sharedInstance;
	}

	// Hide default constructor, only allow access via getBookService() method
	private BookService() {
	}

	public void startSearch(String keyword) {

	}
}
