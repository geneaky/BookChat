package toy.bookchat.bookchat.exception.badrequest;

import static toy.bookchat.bookchat.exception.ErrorCode.NOT_SUPPORTED_PAGING_CONDITION;

public class NotSupportedPagingConditionException extends BadRequestException {

    public NotSupportedPagingConditionException() {
        super(NOT_SUPPORTED_PAGING_CONDITION, "지원하지 않는 페이징 형식입니다.");
    }
}
