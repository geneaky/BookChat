package toy.bookchat.bookchat.exception.notfound.pariticipant;

import static toy.bookchat.bookchat.exception.ErrorCode.PARTICIPANT_NOT_FOUND;

import toy.bookchat.bookchat.exception.notfound.NotFoundException;

public class ParticipantNotFoundException extends NotFoundException {

    public ParticipantNotFoundException() {
        super(PARTICIPANT_NOT_FOUND, "참여자를 찾을 수 없습니다.");
    }
}
