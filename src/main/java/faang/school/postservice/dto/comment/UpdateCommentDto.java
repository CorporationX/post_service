package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record UpdateCommentDto(
        Long authorId,
        Long postId,
        String content,
        LocalDateTime updatedAt
) {
}