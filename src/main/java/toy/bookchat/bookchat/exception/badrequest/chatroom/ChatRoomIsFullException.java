package toy.bookchat.bookchat.exception.badrequest.chatroom;

import static toy.bookchat.bookchat.exception.ErrorCode.CHAT_ROOM_IS_FULL;

import toy.bookchat.bookchat.exception.badrequest.BadRequestException;

public class ChatRoomIsFullException extends BadRequestException {

    public ChatRoomIsFullException() {
        super(CHAT_ROOM_IS_FULL, "채팅방 인원이 전부 찼습니다.");
    }
}
