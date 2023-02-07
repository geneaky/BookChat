package toy.bookchat.bookchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public enum ExceptionResponse {

    BOOK_NOT_FOUND(ResponseEntity.status(HttpStatus.NOT_FOUND).body("도서를 찾을 수 없습니다")),
    BOOK_REPORT_NOT_FOUND(ResponseEntity.status(HttpStatus.NOT_FOUND).body("저장된 독후감이 없습니다.")),
    USER_NOT_FOUND(ResponseEntity.status(HttpStatus.NOT_FOUND).body("등록된 사용자가 아닙니다")),
    NOT_VERIFIED_TOKEN(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.")),
    IMAGE_PROCESSING_FAIL(
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 처리 실패")),
    IMAGE_UPLOAD_FAIL(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("이미지 업로드 실패")),
    USER_ALREADY_EXISTED(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이미 가입된 사용자입니다")),
    EXPIRED_PUBLIC_KEY(ResponseEntity.status(HttpStatus.NOT_FOUND).body("다시 요청해주세요")),
    WRONG_KEY_SPEC(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("잘 못된 키 생성 형식")),
    TOO_MANY_REQUESTS(
        ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("현재 요청이 많습니다, 잠시후 다시 시도해주세요.")),
    BAD_REQUEST(ResponseEntity.status(HttpStatus.BAD_REQUEST).body("올바르지 않은 요청 형식입니다"));

    private final ResponseEntity<String> value;

    ExceptionResponse(ResponseEntity<String> value) {
        this.value = value;
    }

    public ResponseEntity<String> getValue() {
        return value;
    }
}
