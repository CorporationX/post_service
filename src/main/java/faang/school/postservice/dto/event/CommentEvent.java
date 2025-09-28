package faang.school.postservice.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEvent(
        long id,
        long postAuthorId,
        long commentAuthorId,
        long postId,
        String content,
        LocalDateTime createdAt
) {}
