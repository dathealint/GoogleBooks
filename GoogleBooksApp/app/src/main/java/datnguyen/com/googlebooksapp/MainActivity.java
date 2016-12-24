package datnguyen.com.googlebooksapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Model.Book;
import datnguyen.com.googlebooksapp.Service.BookService;
import datnguyen.com.googlebooksapp.Service.BookServiceListener;
import datnguyen.com.googlebooksapp.Service.LoadmoreInterface;
import datnguyen.com.googlebooksapp.Service.NetworkService;

public class MainActivity extends AppCompatActivity {

	private SearchView searchView = null;
	private RecyclerView recycleView = null;
	private TextView tvErrorMessage = null;

	private BookAdapter bookAdapter = null;
	private ArrayList<Book> listBooks = new ArrayList<>();

	private String currentKeyword = "";
	private int totalItems = 0;

	BookServiceListener bookServiceListener = null;
	private static MainActivity mSharedInstance;

	public static MainActivity getSharedInstance() {
		return mSharedInstance;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mSharedInstance = this;
		// grab controls
		searchView = (SearchView) findViewById(R.id.searchView);
		recycleView = (RecyclerView) findViewById(R.id.recycleView);
		tvErrorMessage = (TextView) findViewById(R.id.tvErrorMessage);
		tvErrorMessage.setVisibility(View.GONE);

		LoadmoreInterface loadmoreInterface = new LoadmoreInterface() {
			@Override
			public void onLoadmoreBegin() {
				// search loadmore, result will be added to current list
				startSearch(currentKeyword, listBooks.size());
			}

			@Override
			public void onLoadmoreCompleted() {

			}
		};

		bookAdapter = new BookAdapter(listBooks, loadmoreInterface);

		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
		recycleView.setLayoutManager(mLayoutManager);
		recycleView.setItemAnimator(new DefaultItemAnimator());
		recycleView.setAdapter(bookAdapter);

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
				// clear current search result for a fresh search
				listBooks.clear();
				bookAdapter.notifyDataSetChanged();

				// hide error message if showing
				tvErrorMessage.setText("");
				bookAdapter.setFooterEnabled(true);

				recycleView.setVisibility(View.VISIBLE);
				tvErrorMessage.setVisibility(View.GONE);
				
				startSearch(s, 0);
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
			public void onListBooksReceiveid(ArrayList<Book> books, int totalCount) {
				// add to list and show in recyclerview
				listBooks.addAll(books);
				totalItems = totalCount;

				handleSearchCompleted();
			}

			@Override
			public void onErrorReceived(Error error) {
				//show eror message
				Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
				handleSearchCompleted();
			}
		};

		BookService.getBookService().setServiceListener(bookServiceListener);
	}

	private void handleSearchCompleted() {
		// check network status, show error message if network unavailable
		if (!NetworkService.getNetworkService(this).isNetworkAvailable()) {
			// show network error message
			recycleView.setVisibility(View.GONE);
			tvErrorMessage.setVisibility(View.VISIBLE);

			tvErrorMessage.setText(getString(R.string.text_network_error));
		} else {
			if (totalItems == 0) {
				// hide result view, show error message
				recycleView.setVisibility(View.GONE);
				tvErrorMessage.setVisibility(View.VISIBLE);

				tvErrorMessage.setText(getString(R.string.text_no_result));
			} else {

				if (listBooks.size() > 0 && listBooks.size() < totalItems) {
					bookAdapter.setFooterEnabled(true);
				} else {
					bookAdapter.setFooterEnabled(false);
				}

				bookAdapter.loadmoreCompleted();

				//notify changes
				bookAdapter.notifyItemInserted(listBooks.size());

				recycleView.setVisibility(View.VISIBLE);
				tvErrorMessage.setVisibility(View.GONE);

			}

		}
	}

	/**
	 * Start searching by sending search query to BookService, and update UI when get result
	 *
	 * @param keyword: keyword to search
	 */
	private void startSearch(String keyword, int startIndex) {
		keyword = keyword.trim();
		currentKeyword = keyword;

		Log.v("MAIN", "startSearch keyword: " + keyword + "startIndex: " + startIndex);

		// send keyword search to Service
		BookService.getBookService().startSearch(keyword, startIndex);
	}

}
