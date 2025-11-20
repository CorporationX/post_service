package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CommentEventDto(
        long postId,
        long authorId,
        long commentId,
        LocalDateTime createdAt
) {
}
