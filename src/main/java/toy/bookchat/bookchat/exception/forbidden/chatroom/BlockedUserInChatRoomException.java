package toy.bookchat.bookchat.exception.forbidden.chatroom;

import static toy.bookchat.bookchat.exception.ErrorCode.BLOCKED_USER_IN_CHAT_ROOM;

import toy.bookchat.bookchat.exception.forbidden.ForbiddenException;

public class BlockedUserInChatRoomException extends ForbiddenException {

    public BlockedUserInChatRoomException() {
        super(BLOCKED_USER_IN_CHAT_ROOM, "채팅방 관리자에 의해 차단되었습니다.");
    }
}
