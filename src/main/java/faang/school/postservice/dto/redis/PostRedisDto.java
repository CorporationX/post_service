package faang.school.postservice.dto.redis;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostRedisDto(
        Long id,
        String content,
        Long authorId,
        LocalDateTime publishedAt
) {}
