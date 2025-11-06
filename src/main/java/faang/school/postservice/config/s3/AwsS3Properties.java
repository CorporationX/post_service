package faang.school.postservice.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "services.s3")
public class AwsS3Properties {
    private String endpoint;
    private String secretKey;
    private String accessKey;
    private String region;
}
