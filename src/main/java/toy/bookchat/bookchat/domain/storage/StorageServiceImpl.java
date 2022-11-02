package toy.bookchat.bookchat.domain.storage;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import java.io.IOException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.config.aws.S3Config;
import toy.bookchat.bookchat.domain.storage.exception.ImageUploadToStorageException;

@Service
public class StorageServiceImpl implements StorageService {

    private final AmazonS3Client amazonS3Client;
    private final S3Config s3Config;

    public StorageServiceImpl(AmazonS3Client amazonS3Client, S3Config s3Config) {
        this.amazonS3Client = amazonS3Client;
        this.s3Config = s3Config;
    }

    @Override
    public void upload(MultipartFile multipartFile, String fileName) {
        try {
            amazonS3Client.putObject(s3Config.getBucketName(), fileName,
                multipartFile.getInputStream(), abstractObjectMetadataFrom(multipartFile));
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

    @Override
    public String getFileUrl(String fileName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s3Config.getImageBucketUrl());
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
        stringBuilder.insert(0, s3Config.getImageFolder());
        return stringBuilder.toString();
    }
}
