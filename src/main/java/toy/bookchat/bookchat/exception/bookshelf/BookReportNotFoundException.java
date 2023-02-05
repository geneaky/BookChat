package toy.bookchat.bookchat.exception.bookshelf;

public class BookReportNotFoundException extends RuntimeException {

    public BookReportNotFoundException() {
        super("BookReport Is Not Registered");
    }
}
