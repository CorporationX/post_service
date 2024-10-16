package faang.school.postservice.config.context.post;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "post.verify-task-executor")
public class TaskExecutorParams {
    private int corePoolSize;
    private int maxPoolSize;
    private int queueCapacity;
    private String threadNamePrefix;
}