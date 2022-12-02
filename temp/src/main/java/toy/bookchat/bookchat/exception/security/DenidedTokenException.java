package toy.bookchat.bookchat.exception.security;

public class DenidedTokenException extends RuntimeException {

    public DenidedTokenException(String message) {
        super(message);
    }

    public DenidedTokenException() {
        super("Not Allowed Token Format Exception");
    }
}
