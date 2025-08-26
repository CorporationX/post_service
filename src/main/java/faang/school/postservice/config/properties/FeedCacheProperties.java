package faang.school.postservice.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.cache.feed")
public class FeedCacheProperties {

    private String keyPrefix;
    private int maxSize;
}
