package faang.school.postservice.config.properties;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@ConfigurationProperties(prefix = "task-executor")
@Validated
public class TaskExecutorProperties {

    @NotNull
    @Valid
    private FileUpload fileUpload;

    @NotNull
    @Valid
    private RedisReconnect redisReconnect;

    @NotNull
    @Valid
    private ScheduledTask scheduledTask;

    @NotNull
    @Valid
    private OutboxEventPublisherTask outboxEventPublisherTask;

    @Getter
    @Setter
    public static class FileUpload {

        @NotNull
        @Min(1)
        private Integer corePoolSize;

        @NotNull
        @Min(1)
        private Integer maxPoolSize;

        @NotNull
        @PositiveOrZero
        private Integer queueCapacity;
    }

    @Getter
    @Setter
    public static class RedisReconnect {

        @NotNull
        @Min(1)
        private Integer poolSize;

        @NotNull
        @Min(1)
        private Integer awaitTerminationSeconds;

        @NotBlank
        private String threadNamePrefix;

        @NotNull
        private Boolean daemon;

        @NotNull
        private Boolean removeOnCancelPolicy;

        @NotNull
        private Boolean waitForTasksToCompleteOnShutdown;
    }

    @Getter
    @Setter
    public static class ScheduledTask {

        @NotNull
        @Min(1)
        private Integer poolSize;

        @NotNull
        @Min(1)
        private Integer awaitTerminationSeconds;

        @NotBlank
        private String threadNamePrefix;

        @NotNull
        private Boolean daemon;

        @NotNull
        private Boolean waitForTasksToCompleteOnShutdown;
    }

    @Getter
    @Setter
    public static class OutboxEventPublisherTask {

        @NotNull
        @Min(1)
        private Integer corePoolSize;

        @NotNull
        @Min(1)
        private Integer maxPoolSize;

        @NotNull
        @PositiveOrZero
        private Integer queueCapacity;

        @NotBlank
        private String threadNamePrefix;
    }
}
