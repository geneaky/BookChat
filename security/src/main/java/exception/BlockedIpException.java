package exception;

public class BlockedIpException extends RuntimeException {

    public BlockedIpException() {
        super("You Are Blocked");
    }
}
