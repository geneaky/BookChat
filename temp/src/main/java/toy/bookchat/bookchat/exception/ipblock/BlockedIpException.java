package toy.bookchat.bookchat.exception.ipblock;

public class BlockedIpException extends RuntimeException {

    public BlockedIpException() {
        super("You Are Blocked");
    }
}
