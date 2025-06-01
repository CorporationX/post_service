package faang.school.postservice.dto.redis;

import java.time.LocalDateTime;

public record FeedRedisDto(
        Long postId,
        LocalDateTime publishedAt
) {}
