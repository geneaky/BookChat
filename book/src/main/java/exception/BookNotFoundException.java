package exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException() {
        super("Book Is Not Registered");
    }
}
