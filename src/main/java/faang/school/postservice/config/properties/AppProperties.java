package faang.school.postservice.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("services.s3")
public class AppProperties {

    private String endpoint;
    private String accessKey;
    private String secretKey;
    private int connectionTimeout;
    private int socketTimeout;

}
