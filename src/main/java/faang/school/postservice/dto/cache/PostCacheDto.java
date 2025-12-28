package faang.school.postservice.dto.cache;

import lombok.Builder;

import java.time.Instant;

@Builder
public record PostCacheDto(
        Long id,
        Long authorId,
        String content,
        Instant createdAt,
        Long likeCount
) {
}
