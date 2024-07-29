package toy.bookchat.bookchat.domain.chat;

import lombok.Getter;

@Getter
public class Message {

    private final Integer receiptId;
    private final String message;

    private Message(Integer receiptId, String message) {
        this.receiptId = receiptId;
        this.message = message;
    }

    public static Message of(Integer receiptId, String message) {
        return new Message(receiptId, message);
    }
}
