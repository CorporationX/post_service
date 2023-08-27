package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "thread-pool")
@Data
public class ThreadPoolConfig {
    private int queueCapacity;
    private int maxPoolSize;
    private int corePoolSize;
    private String threadNamePrefix;
}
