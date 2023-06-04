package toy.bookchat.bookchat.exception.notfound.chatroom;

import static toy.bookchat.bookchat.exception.ErrorCode.CHAT_ROOM_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class ChatRoomNotFoundException extends NotFoundException {

    public ChatRoomNotFoundException() {
        super(CHAT_ROOM_NOT_FOUND, "채팅방을 찾을 수 없습니다.");
    }
}
