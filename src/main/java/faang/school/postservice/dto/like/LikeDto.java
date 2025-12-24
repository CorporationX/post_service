package faang.school.postservice.dto.like;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeDto(
        Long id,
        Long userId,
        Long postId,
        Long commentId,
        LocalDateTime createdAt
) {
}
