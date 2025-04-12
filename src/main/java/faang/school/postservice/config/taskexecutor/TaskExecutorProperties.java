package faang.school.postservice.config.taskexecutor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Data
@ConfigurationProperties(prefix = "task-executor.file-upload")
@Validated
@Component
public class TaskExecutorProperties {

    @NotNull
    @Min(1)
    @Max(16)
    private Integer corePoolSize;

    @NotNull
    @Min(1)
    @Max(64)
    private Integer maxPoolSize;

    @NotNull
    @Min(1)
    @Max(500)
    private Integer queueCapacity;
}
