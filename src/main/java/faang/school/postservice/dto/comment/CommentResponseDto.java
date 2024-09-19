package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentResponseDto(
        long id,
        String content,
        long authorId,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}