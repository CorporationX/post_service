package faang.school.postservice.config.properties.feed;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "feed")
public class FeedProperties {

    private ThreadPool threadPool;
    private Queue queue;

    @Getter
    @Setter
    public static class ThreadPool {

        private int initialPoolSize;
        private int maxPoolSize;
    }

    @Getter
    @Setter
    public static class Queue {

        private int capacity;
    }
}
