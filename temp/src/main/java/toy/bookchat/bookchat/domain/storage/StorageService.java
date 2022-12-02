package toy.bookchat.bookchat.domain.storage;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    void upload(MultipartFile multipartFile, String fileName);

    String getFileUrl(String fileName);

    String createFileName(String fileExtension, String uuidFileName, String currentTime);
}
