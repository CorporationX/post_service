package faang.school.postservice.dto.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record CommentEvent(
        @JsonProperty("authorId")
        Long authorId,
        @JsonProperty("postId")
        Long postId,
        @JsonProperty("commentId")
        Long commentId,
        @JsonProperty("title")
        String title) {
}
