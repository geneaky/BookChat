package toy.bookchat.bookchat.exception.badrequest.chatroom;

import static toy.bookchat.bookchat.exception.ErrorCode.NOT_ENOUGH_ROOM_SIZE;

import toy.bookchat.bookchat.exception.badrequest.BadRequestException;

public class NotEnoughRoomSizeException extends BadRequestException {

    public NotEnoughRoomSizeException() {
        super(NOT_ENOUGH_ROOM_SIZE, "기존 채팅방 크기 보다 작을 수 없습니다.");
    }
}
