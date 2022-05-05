package toy.bookchat.bookchat.domain;

import static toy.bookchat.bookchat.utils.constants.ResponseConstants.BOOK_NOT_FOUND;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import toy.bookchat.bookchat.domain.book.exception.BookNotFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookNotFoundException.class)
    public final ResponseEntity<String> handleBookNotFoundException(
        BookNotFoundException exception) {
        log.info("message = {} :: cause = {}", exception.getMessage(), exception.getCause());
        return BOOK_NOT_FOUND;
    }
}
