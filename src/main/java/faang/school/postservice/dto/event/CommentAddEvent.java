package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

/**
 * @author Alexander Bulgakov
 */

public record CommentAddEvent(
        long authorId,
        long postId,
        long authorPostId,
        long commentId,
        String content,
        LocalDateTime createdAt
) {
}
