package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PublishCommentDto(
        Long commentId,
        Long authorId,
        Long postId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}