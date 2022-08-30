package toy.bookchat.bookchat.domain.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService{

    @Override
    public void upload(MultipartFile multipartFile) {

    }
}
