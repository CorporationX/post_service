package faang.school.postservice.model.event;

import java.time.LocalDateTime;

public record CommentEvent(
        Long commentId,
        Long postId,
        Long authorId,
        LocalDateTime createdAt
){}