package faang.school.postservice.dto.comment;

import java.time.Instant;

public record CommentFeedEvent(
        Long commentId,
        Long postId,
        Long authorId,
        String content,
        Instant createdAt
) {
}
