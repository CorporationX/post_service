package faang.school.postservice.config.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {
    private int port;
    private String host;
    private Channels channels;
    private Cache feedCache;
    private Cache postCache;
    private Cache userCache;


    @Data
    public static class Channels {
        private String likesChannel;
        private String commentsChannel;
    }

    @Data
    public static class Cache {
        private Long maxPostsAmount;
        private Long ttl;
        private String keyspace;
    }
}