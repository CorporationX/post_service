package faang.school.postservice.config.news.feed;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server.news.feed.thread.pool")
public class NewsFeedThreadPoolProperties {
    private int corePoolSize;
    private int maximumPoolSize;
    private long keepAliveTime;
    private int queueCapacity;
}
