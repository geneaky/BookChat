package toy.bookchat.bookchat.exception.participant;

public class AlreadyParticipatedException extends RuntimeException {

    public AlreadyParticipatedException() {
        super("Already Participated User");
    }
}
