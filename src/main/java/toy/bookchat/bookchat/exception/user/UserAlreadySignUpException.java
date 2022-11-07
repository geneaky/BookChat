package toy.bookchat.bookchat.exception.user;

public class UserAlreadySignUpException extends RuntimeException {

    public UserAlreadySignUpException() {
        super("User Already Sign Up");
    }
}
