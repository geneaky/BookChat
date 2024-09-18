package toy.bookchat.bookchat.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    String upload(MultipartFile multipartFile, String uuid, String date);
}
