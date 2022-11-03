package toy.bookchat.bookchat.exception.user;

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
