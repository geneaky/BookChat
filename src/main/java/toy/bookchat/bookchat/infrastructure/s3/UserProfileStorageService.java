package toy.bookchat.bookchat.infrastructure.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import toy.bookchat.bookchat.exception.internalserver.ImageUploadToStorageException;

@Service
public class UserProfileStorageService implements StorageService {

  // TODO: 2023/03/08 앞단에서 정확한 이미지 사이즈 제한을 지정하기전까지는 일시적으로 제한을 해제한다.
  private static final int WIDTH_LIMIT = Integer.MAX_VALUE / 2;
  //    private static final int WIDTH_LIMIT = 200;
  private static final int HEIGHT_LIMIT = Integer.MAX_VALUE / 2;
  //    private static final int HEIGHT_LIMIT = 200;

  private final AmazonS3Client amazonS3Client;
  private final StorageProperties storageProperties;
  private final ImageValidator imageValidator;

  public UserProfileStorageService(AmazonS3Client amazonS3Client,
      StorageProperties storageProperties, ImageValidator imageValidator) {
    this.amazonS3Client = amazonS3Client;
    this.storageProperties = storageProperties;
    this.imageValidator = imageValidator;
  }

  @Override
  public String upload(MultipartFile multipartFile, String uuid, String date) {
    try {
      String fileName = createFileName(multipartFile, uuid, date);
      amazonS3Client.putObject(storageProperties.getBucketName(), fileName,
          multipartFile.getInputStream(), abstractObjectMetadataFrom(multipartFile));

      return getFileUrl(fileName);
    } catch (Exception exception) {
      throw new ImageUploadToStorageException();
    }
  }

  private ObjectMetadata abstractObjectMetadataFrom(MultipartFile multipartFile) {
    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentType(multipartFile.getContentType());
    objectMetadata.setContentLength(multipartFile.getSize());
    return objectMetadata;
  }

  private String getFileUrl(String fileName) {
    return storageProperties.getImageBucketUrl() + fileName;
  }

  /**
   * '날짜 역순' + UUID로 저장 - S3가 prefix를 사용하여 partitioning을 하기 때문에
   */
  private String createFileName(MultipartFile file, String uuidFileName,
      String currentTime) {
    imageValidator.hasValidImage(file, WIDTH_LIMIT, HEIGHT_LIMIT);
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(currentTime).reverse();
    stringBuilder.append(uuidFileName);
    stringBuilder.append(".");
    stringBuilder.append(getFileExtension(file));
    stringBuilder.insert(0, storageProperties.getUserProfileImageFolder());
    return stringBuilder.toString();
  }

  private String getFileExtension(MultipartFile image) {
    return image.getOriginalFilename()
        .substring(image.getOriginalFilename().lastIndexOf(".") + 1);
  }
}
