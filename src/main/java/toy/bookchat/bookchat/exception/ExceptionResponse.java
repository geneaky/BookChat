package toy.bookchat.bookchat.exception;

import lombok.Getter;

@Getter
public class ExceptionResponse {

    private ErrorCode errorCode;
    private String message;

    private ExceptionResponse() {
    }

    private ExceptionResponse(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public static ExceptionResponse from(CustomException e) {
        return new ExceptionResponse(e.getErrorCode(), e.getMessage());
    }

    public static ExceptionResponse from(ErrorCode errorCode, String message) {
        return new ExceptionResponse(errorCode, message);
    }
}
