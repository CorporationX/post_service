package faang.school.postservice.dto.post;

import java.time.LocalDateTime;

public record PostDto(
        long id,
        String content,
        long projectId,
        long authorId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
