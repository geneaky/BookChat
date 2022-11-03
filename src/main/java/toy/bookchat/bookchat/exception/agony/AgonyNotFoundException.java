package toy.bookchat.bookchat.exception.agony;

public class AgonyNotFoundException extends RuntimeException {

    public AgonyNotFoundException(String message) {
        super(message);
    }

    public AgonyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public AgonyNotFoundException(Throwable cause) {
        super(cause);
    }

    protected AgonyNotFoundException(String message, Throwable cause, boolean enableSuppression,
        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
