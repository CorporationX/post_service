package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisCredentials {

    private String host;

    private int port;
    private Channels channels;

    @Data
    static class Channels {
        private String achievement;
        private String follower;
        private String post;
        private String postLike;
        private String likePostAnalytics;
    }
}
