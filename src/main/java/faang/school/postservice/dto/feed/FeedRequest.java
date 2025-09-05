package faang.school.postservice.dto.feed;

import io.swagger.v3.oas.annotations.media.Schema;

public record FeedRequest(
        @Schema(description = "Text content of the post")
        Long searchAfter
) {
}
