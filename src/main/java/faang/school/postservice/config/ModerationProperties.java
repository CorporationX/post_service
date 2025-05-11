package faang.school.postservice.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "moderation")
@Validated
@Getter
@Setter
public class ModerationProperties {

    @NotNull
    @Min(1)
    private Integer batchSize;

    @NotNull
    @Min(1)
    private Integer chunkSize;

    @NotNull
    private Resource dictionary;

    @NotNull
    @Min(1)
    private Integer corePoolSize;

    @NotNull
    @Min(1)
    private Integer maxPoolSize;

    @NotNull
    @Min(0)
    private Integer queueCapacity;
}
