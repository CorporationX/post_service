package faang.school.postservice.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "redis")
@AllArgsConstructor
@Data
public class RedisProperty {
    private String host;
    private int port;
}
