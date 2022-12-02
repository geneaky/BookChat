package toy.bookchat.bookchat.exception;

public class NotSupportedPagingConditionException extends RuntimeException {

    public NotSupportedPagingConditionException() {
        super("Not Supported Paging Condition");
    }
}
