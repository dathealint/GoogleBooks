package datnguyen.com.googlebooksapp;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import datnguyen.com.googlebooksapp.Model.Book;

/**
 * Created by datnguyen on 12/22/16.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookHolder> {

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
			// TODO: use HTTP Connection to download thumb image

			this.tvTitle.setText(book.getTitle());
			this.tvAuthor.setText(book.getAuthor());
		}

		@Override
		public void onClick(View view) {
			Log.v("BookHolder", "DID CLICK BOOK HOLDER");
		}
	}

	private ArrayList<Book> books;

	public BookAdapter(ArrayList<Book> books) {
		this.books = books;
	}

	@Override
	public BookAdapter.BookHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View inflatedView = inflater.inflate(R.layout.book_holder_layout, parent, false);
		return new BookHolder(inflatedView);
	}

	@Override
	public void onBindViewHolder(BookAdapter.BookHolder holder, int position) {
		Book book = books.get(position);
		holder.bindBook(book);
	}

	@Override
	public int getItemCount() {
		Log.v("Adapter", "getItemCount: "+ books.size());
		return books.size();
	}

}
