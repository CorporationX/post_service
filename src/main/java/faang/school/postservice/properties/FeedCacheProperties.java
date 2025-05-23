package faang.school.postservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "feed.cache")
@Component
@Getter
@Setter
public class FeedCacheProperties {
    private long ttlSeconds = 86400;
    private int maxFeedSize = 500;
    private int maxComments = 3;
}
