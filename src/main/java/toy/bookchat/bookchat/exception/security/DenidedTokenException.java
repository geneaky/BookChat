package toy.bookchat.bookchat.exception.security;

public class DenidedTokenException extends RuntimeException {

    public DenidedTokenException(String message) {
        super(message);
    }

    public DenidedTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public DenidedTokenException(Throwable cause) {
        super(cause);
    }
}
