package toy.bookchat.bookchat.security.exception;

public class ExpiredPublicKeyCachedException extends
    RuntimeException {

    public ExpiredPublicKeyCachedException(String message) {
        super(message);
    }

    public ExpiredPublicKeyCachedException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpiredPublicKeyCachedException(Throwable cause) {
        super(cause);
    }

    protected ExpiredPublicKeyCachedException(String message, Throwable cause,
        boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
