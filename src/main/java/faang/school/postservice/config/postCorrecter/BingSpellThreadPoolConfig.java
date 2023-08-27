package faang.school.postservice.config.postCorrecter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bing-thread-poll")
@Data
public class BingSpellThreadPoolConfig {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
}
