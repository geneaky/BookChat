package toy.bookchat.bookchat.exception.chatroom;

public class ChatRoomIsFullException extends RuntimeException {

    public ChatRoomIsFullException() {
        super("ChatRoom Is Full");
    }
}
