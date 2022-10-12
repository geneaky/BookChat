package toy.bookchat.bookchat.security.ipblock.exception;

public class BlockedIpException extends RuntimeException {
    public BlockedIpException(String message) {
        super(message);
    }

    public BlockedIpException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockedIpException(Throwable cause) {
        super(cause);
    }

    protected BlockedIpException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
