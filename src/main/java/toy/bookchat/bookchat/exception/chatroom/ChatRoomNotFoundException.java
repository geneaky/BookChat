package toy.bookchat.bookchat.exception.chatroom;

public class ChatRoomNotFoundException extends RuntimeException {

    public ChatRoomNotFoundException() {
        super("ChatRoom Is Not Founded");
    }
}
