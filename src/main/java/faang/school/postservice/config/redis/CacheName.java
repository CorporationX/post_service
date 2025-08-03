package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.data.redis.cache-names")
public record CacheName(
        String posts,
        String feed,
        String authors
) {
}
