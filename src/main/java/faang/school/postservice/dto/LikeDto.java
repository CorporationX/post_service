package faang.school.postservice.dto;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record LikeDto(
        long userId,
        long commentId,
        long postId,
        LocalDateTime createdAt
) {
}
