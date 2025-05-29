package faang.school.postservice.properties.feed;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "executor.feed")
public class FeedExecutorProperties {

    @NotNull
    private Integer corePoolSize;

    @NotNull
    private Integer maxPoolSize;

    @NotNull
    private Integer queueCapacity;

    @NotNull
    private String threadNamePrefix;

}
