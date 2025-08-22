package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;

public record CommentViewDto(
        Long id,
        String content,
        Long authorId,
        Long postId,
        String largeImageFileKey,
        String smallImageFileKey,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long likeCount
) {
}