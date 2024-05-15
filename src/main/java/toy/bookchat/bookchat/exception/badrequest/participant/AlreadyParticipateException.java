package toy.bookchat.bookchat.exception.badrequest.participant;

import static toy.bookchat.bookchat.exception.ErrorCode.ALREADY_PARTICIPATE;

import toy.bookchat.bookchat.exception.badrequest.BadRequestException;

public class AlreadyParticipateException extends BadRequestException {

    public AlreadyParticipateException() {
        super(ALREADY_PARTICIPATE, "이미 참여한 참여자 입니다.");
    }
}
