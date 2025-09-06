package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        LocalDateTime publishedAt,
        LocalDateTime createdAt
) {
}
