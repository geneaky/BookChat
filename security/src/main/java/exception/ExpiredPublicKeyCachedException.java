package exception;

public class ExpiredPublicKeyCachedException extends RuntimeException {

    public ExpiredPublicKeyCachedException() {
        super("Can't Find Public Key, Retry Please");
    }
}
