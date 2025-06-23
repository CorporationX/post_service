package faang.school.postservice.dto.like;

import java.time.LocalDateTime;

public record LikeEvent(
        Long postId,
        Long authorId,
        Long userId,
        LocalDateTime createdAt
) {
}
