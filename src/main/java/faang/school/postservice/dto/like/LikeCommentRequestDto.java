package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikeCommentRequestDto(
        @NotNull(message = "the link to the comment is missing")
        Long commentId
) {
}
