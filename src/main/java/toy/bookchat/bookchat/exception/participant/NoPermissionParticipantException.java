package toy.bookchat.bookchat.exception.participant;

public class NoPermissionParticipantException extends RuntimeException {

    public NoPermissionParticipantException() {
        super("You Are Not Host");
    }
}
