package faang.school.postservice.dto.like;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeDto(
        Long id,
        @NotNull(message = "userId cannot be null")
        Long userId,
        Long postId,
        Long commentId,
        LocalDateTime createdAt
) {
}
