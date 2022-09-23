package toy.bookchat.bookchat.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(MultipartFile multipartFile, String fileName);

    /* TODO: 2022-09-14 업로드 가능한 파일타입 검증 메서드
     */
}
