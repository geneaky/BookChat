package toy.bookchat.bookchat.domain.user.exception;

public class UserAlreadySignUpException extends RuntimeException{
    public UserAlreadySignUpException(String message) {
        super(message);
    }

    public UserAlreadySignUpException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadySignUpException(Throwable cause) {
        super(cause);
    }
}
