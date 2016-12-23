package datnguyen.com.googlebooksapp;

import android.support.v7.app.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Model.Book;
import datnguyen.com.googlebooksapp.Service.BookService;
import datnguyen.com.googlebooksapp.Service.BookServiceListener;

public class MainActivity extends AppCompatActivity {

	private SearchView searchView = null;
	private RecyclerView recycleView = null;

	private BookAdapter bookAdapter = null;
	private ArrayList<Book> listBooks = new ArrayList<>();

	BookServiceListener bookServiceListener = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// grab controls
		searchView = (SearchView) findViewById(R.id.searchView);
		recycleView = (RecyclerView) findViewById(R.id.recycleView);

		bookAdapter = new BookAdapter(listBooks);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recycleView.setLayoutManager(mLayoutManager);
		recycleView.setItemAnimator(new DefaultItemAnimator());
		recycleView.setAdapter(bookAdapter);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				startSearch(s);
				searchView.clearFocus();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String s) {
				return false;
			}
		});

		searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean b) {
				Log.v("MAIN", "onFocusChange: " + b);
			}
		});


		// setup delegate listener
		bookServiceListener = new BookServiceListener() {
			@Override
			public void onListBooksReceiveid(ArrayList<Book> books) {
				// add to list and show in recyclerview
				listBooks.addAll(books);

				//notify changes
				bookAdapter.notifyItemInserted(listBooks.size());
			}

			@Override
			public void onErrorReceived(Error error) {
				//show eror message
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
			}
		};

		BookService.getBookService().setServiceListener(bookServiceListener);
	}

	/**
	 * Start searching by sending search query to BookService, and update UI when get result
	 * @param keyword: keyword to search
	 */
	private void startSearch(String keyword) {
		keyword = keyword.trim();
		Log.v("MAIN", "startSearch keyword: " + keyword);

		// send keyword search to Service
		BookService.getBookService().startSearch(keyword);
	}

}
