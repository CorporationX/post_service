package faang.school.postservice.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "storage.s3")
public class S3Properties {
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private boolean pathStyle = true;
}
