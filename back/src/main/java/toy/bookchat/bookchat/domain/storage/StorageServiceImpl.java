package toy.bookchat.bookchat.domain.storage;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.S3Config;
import toy.bookchat.bookchat.domain.storage.exception.ImageUploadToStorageException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService{

    private final AmazonS3Client amazonS3Client;
    private final S3Config s3Config;

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {
        try {
            amazonS3Client.putObject(s3Config.getBucketName(), fileName,multipartFile.getInputStream(), abstractObjectMetadataFrom(multipartFile));
        } catch (SdkClientException | IOException exception) {
            throw new ImageUploadToStorageException(exception.getMessage(), exception.getCause());
        }
    }

    private ObjectMetadata abstractObjectMetadataFrom(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }
}
