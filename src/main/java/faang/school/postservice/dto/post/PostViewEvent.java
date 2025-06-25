package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record PostViewEvent(
        long postId,
        long authorId,
        long viewerId,
        LocalDateTime createdAt
) {
}
