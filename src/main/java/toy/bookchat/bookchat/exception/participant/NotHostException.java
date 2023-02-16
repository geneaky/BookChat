package toy.bookchat.bookchat.exception.participant;

public class NotHostException extends RuntimeException {

    public NotHostException() {
        super("You Are Not Host");
    }
}
