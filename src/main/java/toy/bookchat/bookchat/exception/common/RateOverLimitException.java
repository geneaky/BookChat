package toy.bookchat.bookchat.exception.common;

public class RateOverLimitException extends RuntimeException {

    public RateOverLimitException() {
        super("Too Many Requests");
    }
}
