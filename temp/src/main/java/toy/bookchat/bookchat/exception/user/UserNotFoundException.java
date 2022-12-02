package toy.bookchat.bookchat.exception.user;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {
        super("Not Registered User");
    }
}
