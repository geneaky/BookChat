package toy.bookchat.bookchat.domain.storage;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.InputStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import toy.bookchat.bookchat.domain.storage.image.ImageValidator;
import toy.bookchat.bookchat.exception.internalserver.ImageUploadToStorageException;
import toy.bookchat.bookchat.infrastructure.aws.StorageProperties;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    StorageProperties storageProperties;
    @Mock
    AmazonS3Client amazonS3Client;
    @Mock
    ImageValidator imageValidator;
    @InjectMocks
    UserProfileStorageService storageService;

    @Test
    void 이미지_파일_업로드_성공() throws Exception {
        MockMultipartFile multipartFile = ImageFixture.createImageFile();
        when(storageProperties.getBucketName()).thenReturn("testBucketName");
        storageService.upload(multipartFile, "test uuid", "test date");
        verify(amazonS3Client).putObject(anyString(), anyString(), any(InputStream.class),
            any(ObjectMetadata.class));
    }

    @Test
    void 이미지_업로드중_발생하는_예외를_커스텀예외로_던지기_성공() throws Exception {
        MockMultipartFile multipartFile = ImageFixture.createImageFile();
        when(amazonS3Client.putObject(any(), any(), any(), any())).thenThrow(
            ImageUploadToStorageException.class);
        when(storageProperties.getBucketName()).thenReturn("testBucketName");
        assertThatThrownBy(() -> {
            storageService.upload(multipartFile, "test uuid", "test date");
        }).isInstanceOf(ImageUploadToStorageException.class);
    }

    private static class ImageFixture {

        public static MockMultipartFile createImageFile() {
            return new MockMultipartFile("test image", "image.webp", "webp",
                "test".getBytes());
        }
    }

}