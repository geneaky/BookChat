package toy.bookchat.bookchat.config.aws;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@Getter
@ConstructorBinding
@ConfigurationProperties(prefix = "aws.s3")
public class S3Config {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucketName;
    private final String imageFolder;
    private final String imageBucketUrl;

    public S3Config(String accessKey, String secretKey, String region, String imageBucketUrl, String bucketName, String imageFolder) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucketName = bucketName;
        this.imageFolder = imageFolder;
        this.imageBucketUrl = imageBucketUrl;
    }
}

