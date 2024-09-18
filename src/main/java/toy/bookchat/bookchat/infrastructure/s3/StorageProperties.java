package toy.bookchat.bookchat.infrastructure.s3;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "aws.s3")
public class StorageProperties {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucketName;
  private final String userProfileImageFolder;
  private final String chatRoomImageFolder;
  private final String imageBucketUrl;

  public StorageProperties(String accessKey, String secretKey, String region, String bucketName,
      String userProfileImageFolder, String chatRoomImageFolder, String imageBucketUrl) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucketName = bucketName;
    this.userProfileImageFolder = userProfileImageFolder;
    this.chatRoomImageFolder = chatRoomImageFolder;
    this.imageBucketUrl = imageBucketUrl;
  }
}
