package toy.bookchat.bookchat.exception.security;

public class WrongKeySpecException extends
    RuntimeException {

    public WrongKeySpecException(String message) {
        super(message);
    }

    public WrongKeySpecException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrongKeySpecException(Throwable cause) {
        super(cause);
    }

    protected WrongKeySpecException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
