package faang.school.postservice.dto.like;

import java.time.Instant;

public record LikeFeedEvent(
        long postId,
        long authorId,
        Long likeId,
        Instant createdAt
) {
}
