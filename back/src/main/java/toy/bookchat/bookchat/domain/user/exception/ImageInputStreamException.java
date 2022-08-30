package toy.bookchat.bookchat.domain.user.exception;

public class ImageInputStreamException extends RuntimeException {

    public ImageInputStreamException(String message) {
        super(message);
    }

    public ImageInputStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageInputStreamException(Throwable cause) {
        super(cause);
    }
}
