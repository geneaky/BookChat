package toy.bookchat.bookchat.domain.storage.exception;

public class ImageUploadToStorageException extends RuntimeException {

    public ImageUploadToStorageException(String message) {
        super(message);
    }

    public ImageUploadToStorageException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImageUploadToStorageException(Throwable cause) {
        super(cause);
    }
}
