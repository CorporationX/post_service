package faang.school.postservice.cache;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("spring.data.redis-news-feed")
public class NewsFeedConfiguration {
    private int feedSize;
    private int userTtl;
    private int postTtl;
    private int commentsLimit;
}
