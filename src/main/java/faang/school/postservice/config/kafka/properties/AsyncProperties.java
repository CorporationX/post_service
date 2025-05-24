package faang.school.postservice.config.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.async")
public class AsyncProperties {
    private ExecutorSettings postEventExecutor;

    @Data
    public static class ExecutorSettings {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}
