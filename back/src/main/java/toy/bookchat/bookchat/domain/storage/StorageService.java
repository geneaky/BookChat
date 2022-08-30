package toy.bookchat.bookchat.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(MultipartFile multipartFile);
}
