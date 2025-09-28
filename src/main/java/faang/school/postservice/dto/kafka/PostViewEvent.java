package faang.school.postservice.dto.kafka;

import java.time.LocalDateTime;

public record PostViewEvent(
        long postId,
        long authorId,
        long viewerId,
        LocalDateTime createdAt
) {
}