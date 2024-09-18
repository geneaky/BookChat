package toy.bookchat.bookchat.infrastructure.s3;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

  private final StorageProperties storageProperties;

  public AWSConfig(StorageProperties storageProperties) {
    this.storageProperties = storageProperties;
  }

  @Bean
  public AmazonS3Client amazonS3Client() {
    BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(
        storageProperties.getAccessKey(),
        storageProperties.getSecretKey());
    return (AmazonS3Client) AmazonS3ClientBuilder
        .standard()
        .withRegion(storageProperties.getRegion())
        .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
        .build();
  }
}
