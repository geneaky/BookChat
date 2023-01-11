package toy.bookchat.bookchat.exception;

import static toy.bookchat.bookchat.exception.ExceptionResponse.BAD_REQUEST;
import static toy.bookchat.bookchat.exception.ExceptionResponse.BOOK_NOT_FOUND;
import static toy.bookchat.bookchat.exception.ExceptionResponse.EXPIRED_PUBLIC_KEY;
import static toy.bookchat.bookchat.exception.ExceptionResponse.IMAGE_PROCESSING_FAIL;
import static toy.bookchat.bookchat.exception.ExceptionResponse.IMAGE_UPLOAD_FAIL;
import static toy.bookchat.bookchat.exception.ExceptionResponse.NOT_VERIFIED_TOKEN;
import static toy.bookchat.bookchat.exception.ExceptionResponse.USER_ALREADY_EXISTED;
import static toy.bookchat.bookchat.exception.ExceptionResponse.USER_NOT_FOUND;
import static toy.bookchat.bookchat.exception.ExceptionResponse.WRONG_KEY_SPEC;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.bookchat.bookchat.exception.agony.AgonyNotFoundException;
import toy.bookchat.bookchat.exception.book.BookNotFoundException;
import toy.bookchat.bookchat.exception.security.DenidedTokenException;
import toy.bookchat.bookchat.exception.security.ExpiredPublicKeyCachedException;
import toy.bookchat.bookchat.exception.security.ExpiredTokenException;
import toy.bookchat.bookchat.exception.security.WrongKeySpecException;
import toy.bookchat.bookchat.exception.storage.ImageUploadToStorageException;
import toy.bookchat.bookchat.exception.user.ImageInputStreamException;
import toy.bookchat.bookchat.exception.user.UserAlreadySignUpException;
import toy.bookchat.bookchat.exception.user.UserNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String LOG_FORMAT = "Class :: {}, Message :: {}";

    @ExceptionHandler(BookNotFoundException.class)
    public final ResponseEntity<String> handleBookNotFoundException(
        BookNotFoundException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return BOOK_NOT_FOUND.getValue();
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return USER_NOT_FOUND.getValue();
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public final ResponseEntity<String> handleExpiredTokenException(
        ExpiredTokenException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return NOT_VERIFIED_TOKEN.getValue();
    }

    @ExceptionHandler(DenidedTokenException.class)
    public final ResponseEntity<String> handleDeniedTokenException(
        DenidedTokenException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return NOT_VERIFIED_TOKEN.getValue();
    }

    @ExceptionHandler(ImageInputStreamException.class)
    public final ResponseEntity<String> handelImageInputStreamException(
        ImageInputStreamException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return IMAGE_PROCESSING_FAIL.getValue();
    }

    @ExceptionHandler(ImageUploadToStorageException.class)
    public final ResponseEntity<String> handleImageUploadToStorageException(
        ImageUploadToStorageException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return IMAGE_UPLOAD_FAIL.getValue();
    }

    @ExceptionHandler(UserAlreadySignUpException.class)
    public final ResponseEntity<String> handleUserAlreadyExistedException(
        UserAlreadySignUpException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return USER_ALREADY_EXISTED.getValue();
    }

    @ExceptionHandler(ExpiredPublicKeyCachedException.class)
    public final ResponseEntity<String> handleExpiredPublicKeyCachedException(
        ExpiredPublicKeyCachedException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return EXPIRED_PUBLIC_KEY.getValue();
    }

    @ExceptionHandler(WrongKeySpecException.class)
    public final ResponseEntity<String> handleWrongKeySpecException(
        WrongKeySpecException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return WRONG_KEY_SPEC.getValue();
    }

    @ExceptionHandler(AgonyNotFoundException.class)
    public final ResponseEntity<String> handleAgonyNotFoundException(
        AgonyNotFoundException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return BAD_REQUEST.getValue();
    }

    @ExceptionHandler(NotSupportedPagingConditionException.class)
    public final ResponseEntity<String> handleNotSupportedPagingConditionException(
        NotSupportedPagingConditionException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return BAD_REQUEST.getValue();
    }

    @MessageExceptionHandler(MessagingException.class)
    public final ResponseEntity<String> handleMissingSessionUserException(
        MessagingException exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return NOT_VERIFIED_TOKEN.getValue();
    }

    @MessageExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleUnExpectedMessagingException(Exception exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return BAD_REQUEST.getValue();
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleUnExpectedException(Exception exception) {
        log.info(LOG_FORMAT, exception.getClass().getSimpleName(), exception.getMessage());
        return BAD_REQUEST.getValue();
    }
}
