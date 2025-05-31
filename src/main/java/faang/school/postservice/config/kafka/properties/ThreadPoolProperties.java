package faang.school.postservice.config.kafka.properties;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "thread-pools")
public class ThreadPoolProperties {
    @NotNull(message = "postEventExecutor settings must be specified")
    private ExecutorProperties postEventExecutor;

    @PostConstruct
    public void validate() {
        if (postEventExecutor.getMaxPoolSize() < postEventExecutor.getCorePoolSize()) {
            throw new IllegalArgumentException("postEventExecutor.maxPoolSize must be greater than or equal to " +
                    "corePoolSize, got: maxPoolSize=" + postEventExecutor.getMaxPoolSize() + ", " +
                    "corePoolSize=" + postEventExecutor.getCorePoolSize());
        }
    }

    @Data
    public static class ExecutorProperties {
        @NotNull(message = "corePoolSize must be specified")
        @Positive(message = "corePoolSize must be positive")
        @Min(value = 1, message = "corePoolSize must be at least 1")
        private Integer corePoolSize;

        @NotNull(message = "maxPoolSize must be specified")
        @Positive(message = "maxPoolSize must be positive")
        @Min(value = 1, message = "maxPoolSize must be at least 1")
        private Integer maxPoolSize;

        @NotNull(message = "queueCapacity must be specified")
        @Positive(message = "queueCapacity must be positive")
        @Min(value = 1, message = "queueCapacity must be at least 1")
        private Integer queueCapacity;

        @NotNull(message = "threadNamePrefix must be specified")
        private String threadNamePrefix;
    }
}