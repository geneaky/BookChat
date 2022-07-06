package toy.bookchat.bookchat.utils.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {

    public static final ResponseEntity<String> BOOK_NOT_FOUND =
        new ResponseEntity<>("도서를 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    public static final ResponseEntity<String> USER_NOT_FOUND =
            new ResponseEntity<>("등록된 사용자가 아닙니다", HttpStatus.NOT_FOUND);
}
