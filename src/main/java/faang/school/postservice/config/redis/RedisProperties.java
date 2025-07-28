package faang.school.postservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
@ConfigurationProperties("spring.data.redis")
public class RedisProperties {
    String host;
    int port;
    boolean active;
    CacheName cacheNames;
    CacheDuration cacheDuration;
    Map<String, String> channels = new HashMap<>();
}
