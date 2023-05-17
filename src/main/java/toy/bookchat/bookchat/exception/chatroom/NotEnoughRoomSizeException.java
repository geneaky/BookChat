package toy.bookchat.bookchat.exception.chatroom;

public class NotEnoughRoomSizeException extends RuntimeException {

    public NotEnoughRoomSizeException() {
        super("Room size have to be more bigger than original room size");
    }
}
