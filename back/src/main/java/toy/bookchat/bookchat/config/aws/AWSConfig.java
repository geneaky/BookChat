package toy.bookchat.bookchat.config.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

    private final S3Config s3Config;

    public AWSConfig(S3Config s3Config) {
        this.s3Config = s3Config;
    }

    @Bean
    public AmazonS3Client amazonS3Client() {
        BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(s3Config.getAccessKey(),
            s3Config.getSecretKey());
        return (AmazonS3Client) AmazonS3ClientBuilder
            .standard()
            .withRegion(s3Config.getRegion())
            .withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
            .build();
    }
}
