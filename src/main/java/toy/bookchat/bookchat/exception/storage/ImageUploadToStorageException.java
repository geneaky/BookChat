package toy.bookchat.bookchat.exception.storage;

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
