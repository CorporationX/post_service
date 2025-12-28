package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEventDto(
        long postId,
        long authorId,
        long commentId,
        String content,
        LocalDateTime createdAt
) {
}
