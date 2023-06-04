package toy.bookchat.bookchat.exception.badrequest.participant;

import static toy.bookchat.bookchat.exception.ErrorCode.NOT_PARTICIPATED;

import toy.bookchat.bookchat.exception.badrequest.BadRequestException;

public class NotParticipatedException extends BadRequestException {

    public NotParticipatedException() {
        super(NOT_PARTICIPATED, "채팅방 참여자가 아닙니다.");
    }
}
