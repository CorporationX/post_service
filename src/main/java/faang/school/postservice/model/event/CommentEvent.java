package faang.school.postservice.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEvent(
        long commentAuthorId,
        String username,
        long postAuthorId,
        long postId,
        String content,
        long commentId,
        LocalDateTime commentedAt
) {
}
