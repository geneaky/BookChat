package toy.bookchat.bookchat.exception.security;

public class NotVerifiedRequestFormatException extends RuntimeException {

    public NotVerifiedRequestFormatException(String message) {
        super(message);
    }
}
