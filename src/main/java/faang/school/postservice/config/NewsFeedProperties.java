package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("server.news.feed")
public class NewsFeedProperties {
    private int newsFeedSize;
    private int limitCommentsOnPost;
    private int batchSize;
}
