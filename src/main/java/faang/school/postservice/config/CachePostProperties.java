package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("post.cache")
public class CachePostProperties {
    private int countHoursTimeToLive;
    private int newsFeedSize;
}
