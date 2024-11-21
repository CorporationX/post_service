package faang.school.postservice.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = "cache.post")
public class PostCacheProperties {
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
