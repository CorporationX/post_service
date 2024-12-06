package faang.school.postservice.properties.redis.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ConfigurationProperties("cache.post")
public class RedisPostProperties {
    private int liveTime;
    private TimeUnit timeUnit;
    private String setKey;
    private Author author;

    @Data
    public static class Author {
        private int liveTime;
        private TimeUnit timeUnit;
        private String setKey;
    }

}
