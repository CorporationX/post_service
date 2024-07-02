package faang.school.postservice.config.redis;

import lombok.Data;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * В этом классе хранятся названия всех топиков (каналов) редиса,
 * получаемые из application.yaml по пути spring.data.redis.channels
 */
@Data
@Setter
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
class RedisProperties {
    private int port;
    private String host;
    private Channels channels;

    @Data
    public static class Channels {
        private String likesChannel;
        private String commentsChannel;
    }
}
