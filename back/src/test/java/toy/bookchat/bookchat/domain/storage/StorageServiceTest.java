package toy.bookchat.bookchat.domain.storage;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    StorageService storageService = new StorageServiceImpl();

    @Test
    public void 이미지_파일_업로드_성공() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        storageService.upload(multipartFile);
    }
}