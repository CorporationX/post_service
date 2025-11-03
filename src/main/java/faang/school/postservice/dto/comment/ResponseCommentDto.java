package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ResponseCommentDto(
        long commentId,
        long authorId,
        String content,
        long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<Long> likesId,
        String largeImageFileKey,
        String smallImageFileKey
) {
}