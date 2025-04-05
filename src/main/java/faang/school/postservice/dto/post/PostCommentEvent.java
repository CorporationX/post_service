package faang.school.postservice.dto.post;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentEvent(
        Long postId,
        Long commentId,
        String content,
        Long authorId,
        LocalDateTime createdAt
) {
}
