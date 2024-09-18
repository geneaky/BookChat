package toy.bookchat.bookchat.infrastructure.s3;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

  String upload(MultipartFile multipartFile, String uuid, String date);
}
