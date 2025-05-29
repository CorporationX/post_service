package faang.school.postservice.properties.feed;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "feed.cache")
public class FeedCacheProperties {

    @NotNull
    @Min(60)
    private Long ttlSeconds;

    @NotNull
    @Min(1)
    private Integer maxFeedSize;

    @NotNull
    @Min(0)
    private Integer maxComments;
}
