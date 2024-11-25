package faang.school.postservice.config.redis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "post.cache")
public class CacheConfigProperties {
    private long defaultTtlSeconds;
    private long publishTtlSeconds;
    private long updateTtlSeconds;
    private long deleteTtlSeconds;
}

