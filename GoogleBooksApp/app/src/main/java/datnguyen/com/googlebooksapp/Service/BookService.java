package datnguyen.com.googlebooksapp.Service;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Constants;
import datnguyen.com.googlebooksapp.Model.Book;

import static datnguyen.com.googlebooksapp.Constants.CONNECTION_TIMEOUT;

/**
 * Created by datnguyen on 12/22/16.
 */

public final class BookService {

	private final static String TAG_NAME = BookService.class.getSimpleName();
	private final static String METHOD_GET = "GET";

	private static BookService sharedInstance;

	private BookServiceListener serviceListener;

	public void setServiceListener(BookServiceListener serviceListener) {
		this.serviceListener = serviceListener;
	}

	public BookServiceListener getServiceListener() {
		return serviceListener;
	}

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

	public void startSearch(final String keyword, final int startIndex) {

		// create async task
		AsyncTask asyncTask = new AsyncTask() {
			@Override
			protected Object doInBackground(Object[] objects) {

				String searchURL = createURL(Constants.URL_BOOKS_SEARCH, new Object[]{keyword, Constants.REQUEST_ITEMS_PER_PAGE, startIndex});
				Log.v(TAG_NAME, "searchURL: " + searchURL);
				String jsonResponse = "";
				try {
					jsonResponse = makeHttpRequest(new URL(searchURL));
				} catch (IOException e) {
					e.printStackTrace();
				}

				return jsonResponse;
			}

			@Override
			protected void onPostExecute(Object jsonString) {
				super.onPostExecute(jsonString);

				try {
					JSONObject rootObject = new JSONObject((String) jsonString);
					JSONArray jsonArray = rootObject.optJSONArray("items");
					ArrayList<Book> listBooks = booksFromJSONArray(jsonArray);

					// get total items, for check loadmore purpose
					int totalItems = rootObject.optInt("totalItems");

					// send result to delegate (listener)
					if (getBookService().getServiceListener() != null) {
						getBookService().getServiceListener().onListBooksReceiveid(listBooks, totalItems);
					}

				} catch (JSONException e) {
					e.printStackTrace();

					// send result to delegate (listener)
					if (getBookService().getServiceListener() != null) {
						Error error = new Error(e.getMessage());
						getBookService().getServiceListener().onErrorReceived(error);
					}
				}
			}
		};

		asyncTask.execute();

	}

	private String createURL(String endPoint, Object[] params) {
		String finalUrl = String.format(endPoint, params);
		finalUrl = Constants.URL_BOOKS_BASE + finalUrl;
		return finalUrl;
	}

	private String makeHttpRequest(URL url) throws IOException {
		String jsonResponse = "";
		HttpURLConnection httpURLConnection = null;
		InputStream inputStream = null;

		try {
			httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod(METHOD_GET);
			httpURLConnection.setConnectTimeout(CONNECTION_TIMEOUT);

			httpURLConnection.connect();

			// read to input stream
			inputStream = httpURLConnection.getInputStream();

			// download and decode string response using String Builder
			StringBuilder stringBuilder = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}

			jsonResponse = stringBuilder.toString();

		} catch (IOException e) {

		} finally {
			// disconnect connection and close input stream
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}

			if (inputStream != null) {
				inputStream.close();
			}
		}

		return jsonResponse;
	}

	private ArrayList<Book> booksFromJSONArray(JSONArray jsonArray) {
		ArrayList<Book> listBooks = new ArrayList<>();

		if (jsonArray == null) {
			return listBooks;
		}

		for (int i = 0; i < jsonArray.length(); i++) {
			JSONObject jsonBook = jsonArray.optJSONObject(i);

			// parse json book object to Book object
			Book newBook = new Book();
			newBook.setBookId(jsonBook.optString("id"));

			JSONObject volumeInfo = jsonBook.optJSONObject("volumeInfo");
			newBook.setTitle(volumeInfo.optString("title"));

			JSONArray listAuthorsJSON = volumeInfo.optJSONArray("authors");
			if (listAuthorsJSON != null) {
				ArrayList<String> listAuthors = new ArrayList<>();
				for (int j = 0; j < listAuthorsJSON.length(); j++) {
					String author = listAuthorsJSON.optString(j);
					listAuthors.add(author);
				}

				String authors = TextUtils.join(", ", listAuthors);
				newBook.setAuthor(authors);
			}

			String description = volumeInfo.optString("description");
			if (description != null) {
				newBook.setDescription(description);
			}

			JSONObject listImages = volumeInfo.optJSONObject("imageLinks");
			if (listImages != null) {
				String thumb = listImages.optString("thumbnail");
				if (thumb == null) {
					thumb = listImages.optString("smallThumbnail");
				}

				if (thumb != null) {
					newBook.setThumbUrl(thumb);
				}
			}


			listBooks.add(newBook);

		}

		return listBooks;
	}

}
