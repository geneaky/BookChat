package toy.bookchat.bookchat.exception.agony;

public class AgonyNotFoundException extends RuntimeException {

    public AgonyNotFoundException() {
        super("Agony Is Not Registered");
    }
}
