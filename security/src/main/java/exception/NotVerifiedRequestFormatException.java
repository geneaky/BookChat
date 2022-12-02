package exception;

public class NotVerifiedRequestFormatException extends RuntimeException {

    public NotVerifiedRequestFormatException(String message) {
        super(message);
    }
}
