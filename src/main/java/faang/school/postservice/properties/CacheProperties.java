package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties("post-service.cache")
@Data
public class CacheProperties {
    private Duration postTtl;
    private Duration authorTtl;
    private Duration feedTtl;
    private int cacheFeedSize;
}
