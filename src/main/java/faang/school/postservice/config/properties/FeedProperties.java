package faang.school.postservice.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "feed")
public class FeedProperties {

    @NotNull
    @Min(1)
    private Integer maxFeedSize;

    @NotNull
    @Min(1)
    private Integer maxComments;

    @NotNull
    @Min(1)
    private Long feedTtl;

    @NotNull
    @Min(1)
    private Long postTtl;

    @NotNull
    @Min(1)
    private Long eventIdTtl;

    @NotNull
    @Min(1)
    private Integer CacheWarmerUserBatchSize;
}
