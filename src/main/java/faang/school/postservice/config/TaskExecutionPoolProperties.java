package faang.school.postservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.task.execution.pool")
public class TaskExecutionPoolProperties {
    private int coreSize;
    private int maxSize;
    private int queueCapacity;
    private int keepAlive;
    private String threadName;
}
