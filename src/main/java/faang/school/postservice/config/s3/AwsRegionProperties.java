package faang.school.postservice.config.s3;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cloud.aws.region")
@Data
public class AwsRegionProperties {
    private String staticRegion;
}
