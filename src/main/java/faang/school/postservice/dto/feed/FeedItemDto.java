package faang.school.postservice.dto.feed;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record FeedItemDto(
        long postId
) {
}
