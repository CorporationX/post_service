package faang.school.postservice.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.redis")
public record RedisProperties(
        int port,
        String host,
        int ttlDaysPosts,
        int ttlDaysUsers
) {}
