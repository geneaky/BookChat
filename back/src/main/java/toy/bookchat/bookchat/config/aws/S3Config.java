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
    private final String imageBucketUrl;
    private final String bucketName;

    public S3Config(String accessKey, String secretKey, String region, String imageBucketUrl, String bucketName) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.imageBucketUrl = imageBucketUrl;
        this.bucketName = bucketName;
    }
}

