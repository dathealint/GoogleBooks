package datnguyen.com.googlebooksapp.Model;

/**
 * Created by datnguyen on 12/22/16.
 */

public class Book {

	private String bookId = null;
	private String title = null;
	private String author = null;
	private String thumbUrl = null;

	public String getBookId() {
		return bookId;
	}

	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}

	public void setThumbUrl(String thumbUrl) {
		this.thumbUrl = thumbUrl;
	}
}
