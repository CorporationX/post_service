package faang.school.postservice.config.properties.cache.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "feed.cache.comment")
public record CommentCacheProperties(
        @NotBlank String listKeyPrefix,
        @NotBlank String idsKeyPrefix,
        @NotBlank String setKeyPrefix,
        @Positive int maxSize
) {}
