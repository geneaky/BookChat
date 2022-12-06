package toy.bookchat.bookchat.domain.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.StorageProperties;
import toy.bookchat.bookchat.exception.storage.ImageUploadToStorageException;

@Service
public class UserProfileStorageService implements StorageService {

    private final AmazonS3Client amazonS3Client;
    private final StorageProperties storageProperties;

    public UserProfileStorageService(AmazonS3Client amazonS3Client,
        StorageProperties storageProperties) {
        this.amazonS3Client = amazonS3Client;
        this.storageProperties = storageProperties;
    }

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {
        try {
            amazonS3Client.putObject(storageProperties.getBucketName(), fileName,
                multipartFile.getInputStream(), abstractObjectMetadataFrom(multipartFile));
        } catch (SdkClientException | IOException exception) {
            throw new ImageUploadToStorageException(exception.getMessage());
        }
    }

    private ObjectMetadata abstractObjectMetadataFrom(MultipartFile multipartFile) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());
        return objectMetadata;
    }

    @Override
    public String getFileUrl(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(storageProperties.getImageBucketUrl());
        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }

    /**
     * '날짜 역순' + UUID로 저장 - S3가 prefix를 사용하여 partitioning을 하기 때문에
     */
    @Override
    public String createFileName(String fileExtension, String uuidFileName, String currentTime) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(currentTime).reverse();
        stringBuilder.append(uuidFileName);
        stringBuilder.append(".");
        stringBuilder.append(fileExtension);
        stringBuilder.insert(0, storageProperties.getUserProfileImageFolder());
        return stringBuilder.toString();
    }
}
