package toy.bookchat.bookchat.domain.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.S3Config;
import toy.bookchat.bookchat.domain.storage.exception.ImageUploadToStorageException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StorageServiceTest {

    @Mock
    S3Config s3Config;
    @Mock
    AmazonS3Client amazonS3Client;

    @InjectMocks
    StorageServiceImpl storageService;

    @Test
    void 이미지_파일_업로드_성공() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Config.getBucketName()).thenReturn("testBucketName");
        when(multipartFile.getInputStream()).thenReturn(mock(InputStream.class));
        storageService.upload(multipartFile,"test");
        verify(amazonS3Client).putObject(anyString(), anyString(), any(InputStream.class), any(ObjectMetadata.class));
    }

    @Test
    void 이미지_업로드중_S3예외_발생시_커스텀예외_던지기_성공() throws Exception {
        when(amazonS3Client.putObject(any(),any(), any(), any())).thenThrow(ImageUploadToStorageException.class);
        when(s3Config.getBucketName()).thenReturn("testBucketName");
        assertThatThrownBy(() -> {
            storageService.upload(mock(MultipartFile.class), "test");
        }).isInstanceOf(ImageUploadToStorageException.class);
    }

    @Test
    void 이미지_업로드중_IO예외_발생시_커스텀예외_던지기_성공() throws Exception {
        MultipartFile multipartFile = mock(MultipartFile.class);
        when(s3Config.getBucketName()).thenReturn("testBucketName");
        when(multipartFile.getInputStream()).thenThrow(IOException.class);
        assertThatThrownBy(() -> {
            storageService.upload(multipartFile, "test");
        }).isInstanceOf(ImageUploadToStorageException.class);
    }

    @Test
    void 파일이름으로_S3_오브젝트_URI생성() throws Exception {

        when(s3Config.getImageBucketUrl()).thenReturn("www//s3bucket/");
        String fileUrl = storageService.getFileUrl("test");

        assertThat(fileUrl).isEqualTo("www//s3bucket/test");
    }

    @Test
    void S3_오브젝트_파일이름_생성() throws Exception {
        when(s3Config.getImageFolder()).thenReturn("test/");

        String fileName = storageService.createFileName("webp", "1234", "2022-10-12");

        assertThat(fileName).isEqualTo("test/21-01-22021234.webp");
    }
}