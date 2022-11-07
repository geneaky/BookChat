package toy.bookchat.bookchat.exception.book;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("Book Is Not Registered");
    }
}
