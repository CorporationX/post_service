package faang.school.postservice.dto.post;

import java.time.Instant;
import java.util.List;

public record PostFeedEvent(
        Long postId,
        Long authorId,
        List<Long> followerIds,
        Instant publishedAt
) {
}
