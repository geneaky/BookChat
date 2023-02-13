package toy.bookchat.bookchat.exception.participant;

public class ParticipantNotFoundException extends RuntimeException {

    public ParticipantNotFoundException() {
        super("Not Registered Participant");
    }
}
