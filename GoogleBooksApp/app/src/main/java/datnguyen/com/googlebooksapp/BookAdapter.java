package datnguyen.com.googlebooksapp;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URL;
import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Model.Book;
import datnguyen.com.googlebooksapp.Service.LoadmoreInterface;

/**
 * Created by datnguyen on 12/22/16.
 */

public class BookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private final int VIEW_TYPE_ITEM = 1;
	private final int VIEW_TYPE_PROGRESSBAR = 0;
	private boolean isFooterEnabled = false;

	private boolean isLoadingmore = false;

	private LoadmoreInterface loadmoreInterface;

	private ArrayList<Book> books;
	public BookAdapter(ArrayList<Book> books, LoadmoreInterface loadmoreInterface) {
		this.books = books;
		this.loadmoreInterface = loadmoreInterface;
	}

	public void setFooterEnabled(boolean footerEnabled) {
		isFooterEnabled = footerEnabled;
	}

	public void loadmoreCompleted() {
		isLoadingmore = false;
	}

	// nested class for ViewHolder
	public static class BookHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

		private ImageView imvThumb = null;
		private TextView tvTitle = null;
		private TextView tvAuthor = null;
		private Book book = null;

		public BookHolder(View itemView) {
			super(itemView);
			this.imvThumb = (ImageView) itemView.findViewById(R.id.imvThumb);
			this.tvAuthor = (TextView) itemView.findViewById(R.id.tvAuthor);
			this.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

			itemView.setOnClickListener(this);
		}

		public void bindBook(Book book) {
			this.book = book;

			// get imageUrl and use connection to download image
			String imageUrl = book.getThumbUrl();
			if (imageUrl != null) {
				Glide.with(MainActivity.getSharedInstance().getApplicationContext()).load(imageUrl).into(this.imvThumb);
			} else {
				 // show default image
			}

			this.tvTitle.setText(book.getTitle());
			this.tvAuthor.setText(book.getAuthor());
		}

		@Override
		public void onClick(View view) {
			Log.v("BookHolder", "DID CLICK BOOK HOLDER");
		}
	}

	public static class LoadmoreHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
		private ProgressBar progressBar = null;
		public LoadmoreHolder(View itemView) {
			super(itemView);
			this.progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
		}

		public void bindLoadmore(boolean isLoading) {
			this.progressBar.setIndeterminate(isLoading);
		}

		@Override
		public void onClick(View view) {
			Log.v("BookHolder", "DID CLICK BOOK HOLDER");
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		ViewHolder viewHolder;
		if (viewType == VIEW_TYPE_ITEM) {
			View inflatedView = inflater.inflate(R.layout.book_holder_layout, parent, false);
			viewHolder = new BookHolder(inflatedView);
		} else {
			View inflatedView = inflater.inflate(R.layout.loadmore_holder_layout, parent, false);
			viewHolder = new LoadmoreHolder(inflatedView);
		}

		return viewHolder;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof BookHolder) {
			Book book = books.get(position);
			((BookHolder) holder).bindBook(book);
		} else {
			((LoadmoreHolder) holder).bindLoadmore(true);
			if (!isLoadingmore && loadmoreInterface != null && position == books.size()) {
				loadmoreInterface.onLoadmoreBegin();
				isLoadingmore = true;
			}
		}
	}

	@Override
	public int getItemCount() {
		Log.v("Adapter", "getItemCount: "+ books.size());

		return (isFooterEnabled) ? books.size() + 1 : books.size();
	}

	@Override
	public int getItemViewType(int position) {
		if (isFooterEnabled && position >= books.size()) {
			return VIEW_TYPE_PROGRESSBAR;
		}
		return VIEW_TYPE_ITEM;
	}
}
