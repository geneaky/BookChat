package toy.bookchat.bookchat.exception.toomanyrequests;

import static toy.bookchat.bookchat.exception.ErrorCode.RATE_OVER_LIMIT;

public class RateOverLimitException extends TooManyRequestException {

    public RateOverLimitException() {
        super(RATE_OVER_LIMIT, "현재 요청이 많습니다, 잠시후 다시 시도해주세요.");
    }
}
