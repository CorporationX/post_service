package faang.school.postservice.config.cache;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CacheConfig {

    @Value("${spring.data.cache.expiration}")
    private long cacheExpirationInSeconds;

    public Duration getCacheExpiration() {
        return Duration.ofSeconds(cacheExpirationInSeconds);
    }
}