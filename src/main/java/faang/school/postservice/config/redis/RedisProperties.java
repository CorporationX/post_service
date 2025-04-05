package faang.school.postservice.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(
        int port,
        String host,
        Cache cache
) {
    public record Cache(
            int ttlMinutes,
            int userTtlMinutes,
            int postTtlMinutes,
            int commentTtlMinutes,
            String userCacheName,
            String postCacheName,
            String commentCacheName) {
    }
}
