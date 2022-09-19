package toy.bookchat.bookchat.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;
import toy.bookchat.bookchat.domain.storage.exception.ImageUploadToStorageException;
import toy.bookchat.bookchat.domain.user.exception.ImageInputStreamException;
import toy.bookchat.bookchat.domain.user.exception.UserAlreadySignUpException;
import toy.bookchat.bookchat.domain.user.exception.UserNotFoundException;
import toy.bookchat.bookchat.security.exception.DenidedTokenException;
import toy.bookchat.bookchat.security.exception.ExpiredTokenException;

import javax.validation.ConstraintViolationException;

import static toy.bookchat.bookchat.utils.constants.ResponseConstants.*;

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
    public final ResponseEntity<String> handleExpiredTokenException(ExpiredTokenException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return NOT_VERIFIED_TOKEN;
    }

    @ExceptionHandler(DenidedTokenException.class)
    public final ResponseEntity<String> handleDeniedTokenException(DenidedTokenException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return NOT_VERIFIED_TOKEN;
    }

    @ExceptionHandler(ImageInputStreamException.class)
    public final ResponseEntity<String> handelImageInputStreamException(ImageInputStreamException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return IMAGE_PROCESSING_FAIL;
    }

    @ExceptionHandler(ImageUploadToStorageException.class)
    public final ResponseEntity<String> handleImageUploadToStorageException(ImageUploadToStorageException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return IMAGE_UPLOAD_FAIL;
    }

    @ExceptionHandler(UserAlreadySignUpException.class)
    public final ResponseEntity<String> handleUserAlreadyExistedException(UserAlreadySignUpException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return USER_ALREADY_EXISTED;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return CONSTRAINT_VIOLATION;
    }
}
