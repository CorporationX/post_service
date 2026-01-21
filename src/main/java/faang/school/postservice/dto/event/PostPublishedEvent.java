package faang.school.postservice.dto.event;

import java.time.LocalDateTime;
import java.util.List;

public record PostPublishedEvent(
        Long postId,
        Long authorId,
        String content,
        List<Long> followersIds,
        LocalDateTime publishedAt
) {
}
