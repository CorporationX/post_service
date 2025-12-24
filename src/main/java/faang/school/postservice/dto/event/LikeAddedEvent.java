package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

public record LikeAddedEvent(
        Long likeId,
        Long userId,
        Long postId,
        LocalDateTime createdAt
) {
}
