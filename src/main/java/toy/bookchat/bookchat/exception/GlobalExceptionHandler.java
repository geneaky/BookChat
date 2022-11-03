package toy.bookchat.bookchat.exception;

import static toy.bookchat.bookchat.utils.constants.ResponseConstants.BAD_REQUEST;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.BOOK_NOT_FOUND;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.CONSTRAINT_VIOLATION;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.EXPIRED_PUBLIC_KEY;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.IMAGE_PROCESSING_FAIL;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.IMAGE_UPLOAD_FAIL;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.NOT_VERIFIED_TOKEN;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.USER_ALREADY_EXISTED;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.USER_NOT_FOUND;
import static toy.bookchat.bookchat.utils.constants.ResponseConstants.WRONG_KEY_SPEC;

import javax.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
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

    @ExceptionHandler(BookNotFoundException.class)
    public final ResponseEntity<String> handleBookNotFoundException(
        BookNotFoundException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BOOK_NOT_FOUND;
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<String> handleUserNotFoundException(
        UserNotFoundException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return USER_NOT_FOUND;
    }

    @ExceptionHandler(ExpiredTokenException.class)
    public final ResponseEntity<String> handleExpiredTokenException(
        ExpiredTokenException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return NOT_VERIFIED_TOKEN;
    }

    @ExceptionHandler(DenidedTokenException.class)
    public final ResponseEntity<String> handleDeniedTokenException(
        DenidedTokenException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return NOT_VERIFIED_TOKEN;
    }

    @ExceptionHandler(ImageInputStreamException.class)
    public final ResponseEntity<String> handelImageInputStreamException(
        ImageInputStreamException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return IMAGE_PROCESSING_FAIL;
    }

    @ExceptionHandler(ImageUploadToStorageException.class)
    public final ResponseEntity<String> handleImageUploadToStorageException(
        ImageUploadToStorageException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return IMAGE_UPLOAD_FAIL;
    }

    @ExceptionHandler(UserAlreadySignUpException.class)
    public final ResponseEntity<String> handleUserAlreadyExistedException(
        UserAlreadySignUpException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return USER_ALREADY_EXISTED;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<String> handleConstraintViolationException(
        ConstraintViolationException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return CONSTRAINT_VIOLATION;
    }

    @ExceptionHandler(ExpiredPublicKeyCachedException.class)
    public final ResponseEntity<String> handleExpiredPublicKeyCachedException(
        ExpiredPublicKeyCachedException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return EXPIRED_PUBLIC_KEY;
    }

    @ExceptionHandler(WrongKeySpecException.class)
    public final ResponseEntity<String> handleWrongKeySpecException(
        WrongKeySpecException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return WRONG_KEY_SPEC;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public final ResponseEntity<String> handleIllegalArgumentException(
        IllegalArgumentException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BAD_REQUEST;
    }

    @ExceptionHandler(MissingRequestValueException.class)
    public final ResponseEntity<String> handleMissingRequestValueException(
        MissingRequestValueException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BAD_REQUEST;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<String> handleDataIntegrityViolationException(
        DataIntegrityViolationException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BAD_REQUEST;
    }

    @ExceptionHandler(AgonyNotFoundException.class)
    public final ResponseEntity<String> handleAgonyNotFoundException(
        AgonyNotFoundException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BAD_REQUEST;
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleUnExpectedException(Exception exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BAD_REQUEST;
    }
}
