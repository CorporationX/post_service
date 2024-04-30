package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record LikeAddEvent(
        long likeId,
        long authorId,
        long postId,
        LocalDateTime createdAt
) {
}
