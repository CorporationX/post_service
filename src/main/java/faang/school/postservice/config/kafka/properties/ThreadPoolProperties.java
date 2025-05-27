package faang.school.postservice.config.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "thread-pools")
public class ThreadPoolProperties {

    private ExecutorProperties postEventExecutor;

    @Data
    public static class ExecutorProperties {
        private int corePoolSize;
        private int maxPoolSize;
        private int queueCapacity;
        private String threadNamePrefix;
    }
}