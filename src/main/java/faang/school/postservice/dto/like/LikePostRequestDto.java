package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikePostRequestDto(
    @NotNull(message = "user is empty")
    Long userId,
    @NotNull(message = "post is empty")
    Long postId
) {
}
