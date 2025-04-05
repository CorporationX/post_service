package faang.school.postservice.config.aws;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "aws")
public class AwsProperties {
    private String url;
    private String user;
    private String password;
    private String bucketName;
}
