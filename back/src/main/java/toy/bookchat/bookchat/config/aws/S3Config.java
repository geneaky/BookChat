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

    public S3Config(String accessKey, String secretKey, String region) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
    }
}

