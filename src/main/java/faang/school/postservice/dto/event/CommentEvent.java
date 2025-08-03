package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

public record CommentEvent(
        Long commentId,
        Long postId,
        Long authorId,
        LocalDateTime createdAt
) {
}