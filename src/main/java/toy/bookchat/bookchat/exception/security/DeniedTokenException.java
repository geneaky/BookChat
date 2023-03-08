package toy.bookchat.bookchat.exception.security;

public class DeniedTokenException extends RuntimeException {

    public DeniedTokenException(String message) {
        super(message);
    }

    public DeniedTokenException() {
        super("Not Allowed Token Format Exception");
    }
}
