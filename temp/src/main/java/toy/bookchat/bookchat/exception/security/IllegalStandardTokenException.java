package toy.bookchat.bookchat.exception.security;

public class IllegalStandardTokenException extends RuntimeException {

    public IllegalStandardTokenException() {
        super("Some Token Parts Are Not Existed");
    }
}
