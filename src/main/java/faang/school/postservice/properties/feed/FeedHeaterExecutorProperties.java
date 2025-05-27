package faang.school.postservice.properties.feed;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "executor.feed-heater")
public class FeedHeaterExecutorProperties {

    @NotNull
    private Integer corePoolSize;

    @NotNull
    private Integer maxPoolSize;

    @NotNull
    private Integer queueCapacity;

    @NotNull
    private String threadNamePrefix;
}

