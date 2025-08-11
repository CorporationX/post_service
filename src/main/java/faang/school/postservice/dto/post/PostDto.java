package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record PostDto(
        Long id,
        String content,
        Long authorId,
        Long projectId,
        boolean published,
        LocalDateTime publishedAt,
        boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        long numberOfLikes
) {
}
