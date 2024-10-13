package faang.school.postservice.config.redis;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
@Getter
public class RedisPropertiesConfiguration {

    @Value("${data.redis.channel.like-events}")
    private String likeEvents;
}
