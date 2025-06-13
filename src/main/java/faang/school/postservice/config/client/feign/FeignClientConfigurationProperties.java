package faang.school.postservice.config.client.feign;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "system")
@Data
@Component
public class FeignClientConfigurationProperties {
    private long userId;
}
