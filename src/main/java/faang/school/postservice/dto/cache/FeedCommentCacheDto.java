package faang.school.postservice.dto.cache;

import java.time.Instant;

public record FeedCommentCacheDto(
        long id,
        long authorId,
        String content,
        Instant createdAt
) {
}
