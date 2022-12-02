package toy.bookchat.bookchat.exception.security;

public class WrongKeySpecException extends RuntimeException {

    public WrongKeySpecException() {
        super("Wrong KeySpec");
    }
}
