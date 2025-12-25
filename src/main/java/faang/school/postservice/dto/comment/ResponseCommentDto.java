package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record ResponseCommentDto(
        Long id,
        String content,
        Long authorId,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}