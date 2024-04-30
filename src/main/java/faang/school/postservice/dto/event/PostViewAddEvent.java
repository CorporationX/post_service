package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record PostViewAddEvent(
        long id,
        long viewerId,
        long postId,
        LocalDateTime viewedAt
) {
}
