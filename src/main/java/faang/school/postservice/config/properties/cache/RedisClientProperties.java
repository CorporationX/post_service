package faang.school.postservice.config.properties.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "app.redis")
public record RedisClientProperties(
        @DefaultValue("localhost") String host,
        @DefaultValue("6379") int port
) {
}
