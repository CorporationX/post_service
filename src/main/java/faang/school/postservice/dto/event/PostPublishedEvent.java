package faang.school.postservice.dto.event;

import java.time.LocalDateTime;

public record PostPublishedEvent(
        Long postId,
        Long authorId,
        String content,
        LocalDateTime publishedAt
) {
}
