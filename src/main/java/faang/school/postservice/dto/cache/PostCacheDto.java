package faang.school.postservice.dto.cache;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCacheDto(
        Long id,
        Long authorId,
        Long projectId,
        String content,
        boolean published,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime publishedAt,
        LocalDateTime scheduledAt
) {
}