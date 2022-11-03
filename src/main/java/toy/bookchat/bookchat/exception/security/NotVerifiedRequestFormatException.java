package toy.bookchat.bookchat.exception.security;

public class NotVerifiedRequestFormatException extends RuntimeException {

    public NotVerifiedRequestFormatException(String message) {
        super(message);
    }

    public NotVerifiedRequestFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotVerifiedRequestFormatException(Throwable cause) {
        super(cause);
    }

    protected NotVerifiedRequestFormatException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
