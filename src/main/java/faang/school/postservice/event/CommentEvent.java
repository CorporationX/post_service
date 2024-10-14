package faang.school.postservice.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEvent(
        long authorId,
        long commentId,
        LocalDateTime commentedAt
) {
}
