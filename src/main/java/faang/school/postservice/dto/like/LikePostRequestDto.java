package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record LikePostRequestDto(
        @NotNull(message = "the link to the post is missing")
        Long postId
) {
}
