package faang.school.postservice.dto.cache;

import java.time.LocalDateTime;

public record PostCacheDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
