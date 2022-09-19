package toy.bookchat.bookchat.utils.constants;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseConstants {

    public static final ResponseEntity<String> BOOK_NOT_FOUND =
        new ResponseEntity<>("도서를 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    public static final ResponseEntity<String> USER_NOT_FOUND =
        new ResponseEntity<>("등록된 사용자가 아닙니다", HttpStatus.NOT_FOUND);

    public static final ResponseEntity<String> NOT_VERIFIED_TOKEN =
            ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body("유효하지 않은 토큰입니다.");

    public static final ResponseEntity<String> IMAGE_PROCESSING_FAIL =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 처리 실패");

    public static final ResponseEntity<String> IMAGE_UPLOAD_FAIL =
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패");

    public static final ResponseEntity<String> USER_ALREADY_EXISTED =
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 사용자입니다");

    public static final ResponseEntity<String> CONSTRAINT_VIOLATION =
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유요하지 않은 요청입니다.");
}
