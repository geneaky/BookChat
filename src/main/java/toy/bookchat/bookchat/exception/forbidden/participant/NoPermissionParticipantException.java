package toy.bookchat.bookchat.exception.forbidden.participant;

import static toy.bookchat.bookchat.exception.ErrorCode.NO_PERMISSION_PARTICIPANT;

import toy.bookchat.bookchat.exception.forbidden.ForbiddenException;

public class NoPermissionParticipantException extends ForbiddenException {

    public NoPermissionParticipantException() {
        super(NO_PERMISSION_PARTICIPANT, "방장이 아닙니다.");
    }
}
