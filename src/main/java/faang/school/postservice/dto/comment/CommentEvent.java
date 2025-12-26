package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CommentEvent(
    long postId,
    long authorId,
    long commentId,
    LocalDateTime createdAt
        ) {}
