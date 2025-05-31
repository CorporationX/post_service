package faang.school.postservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PostDto(
    Long id,
    @NotNull(message = "post content is empty")
    String content,
    @NotNull(message = "post author is empty")
    Long authorId,
    Long projectId,
    Long likePostCount,
    Long likeCommentCount
) {
}
