package toy.bookchat.bookchat.exception;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.bookchat.bookchat.exception.badrequest.BadRequestException;
import toy.bookchat.bookchat.exception.conflict.ConflictException;
import toy.bookchat.bookchat.exception.forbidden.ForbiddenException;
import toy.bookchat.bookchat.exception.internalserver.InternalServerException;
import toy.bookchat.bookchat.exception.notfound.NotFoundException;
import toy.bookchat.bookchat.exception.toomanyrequests.TooManyRequestException;
import toy.bookchat.bookchat.exception.unauthorized.UnauthorizedException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final String LOG_FORMAT = "Class :: {}, Code :: {}, Message :: {}";
    private final String UNKNOWN = "Unknown";

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleBadRequestException(
        BadRequestException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.badRequest().body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ExceptionResponse> handleUnauthorizedException(
        UnauthorizedException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(ForbiddenException.class)
    public final ResponseEntity<ExceptionResponse> handleForbiddenException(ForbiddenException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(NotFoundException.class)
    public final ResponseEntity<ExceptionResponse> handleNotFoundException(NotFoundException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(NOT_FOUND).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(TooManyRequestException.class)
    public final ResponseEntity<ExceptionResponse> handleTooManyRequestException(
        TooManyRequestException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(TOO_MANY_REQUESTS).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(ConflictException.class)
    public final ResponseEntity<ExceptionResponse> handleConflictException(ConflictException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(CONFLICT).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<ExceptionResponse> handleInternalServerException(
        InternalServerException e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), e.getErrorCode().getValue(),
            e.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse.from(e));
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> handleUnExpectedException(Exception e) {
        log.info(LOG_FORMAT, e.getClass().getSimpleName(), UNKNOWN, e.getMessage());
        return ResponseEntity.badRequest()
            .body(ExceptionResponse.from(ErrorCode.INTERNAL_SERVER, "예상치 못한 예외가 발생했습니다."));
    }
}
