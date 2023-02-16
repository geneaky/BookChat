package toy.bookchat.bookchat.exception.security;

public class NotVerifiedIdTokenException extends RuntimeException {

    public NotVerifiedIdTokenException() {
        super("Not Verified Id Token Exception");
    }
}
