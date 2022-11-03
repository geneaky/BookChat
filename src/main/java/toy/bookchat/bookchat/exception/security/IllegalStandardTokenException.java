package toy.bookchat.bookchat.exception.security;

public class IllegalStandardTokenException extends RuntimeException {

    public IllegalStandardTokenException(String message) {
        super(message);
    }

    public IllegalStandardTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalStandardTokenException(Throwable cause) {
        super(cause);
    }

    protected IllegalStandardTokenException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
