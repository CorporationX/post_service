package faang.school.postservice.config.redis.master;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties("spring.data.redis-master")
public class RedisProperties {
    String host;
    int port;
    Map<String, String> channels = new HashMap<>();
}
