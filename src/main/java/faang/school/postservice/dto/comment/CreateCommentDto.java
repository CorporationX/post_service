package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CreateCommentDto(
        Long authorId,
        Long postId,
        String content,
        LocalDateTime createdAt
) {
}