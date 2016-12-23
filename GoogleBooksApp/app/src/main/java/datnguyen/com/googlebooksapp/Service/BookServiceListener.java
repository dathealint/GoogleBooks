package datnguyen.com.googlebooksapp.Service;

import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Model.Book;

/**
 * Created by datnguyen on 12/23/16.
 */

public interface BookServiceListener {

	void onListBooksReceiveid(ArrayList<Book> books);

	void onErrorReceived(Error error);

}
