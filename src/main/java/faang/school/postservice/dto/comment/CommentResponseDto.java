package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record CommentResponseDto(
        long id,
        String content,
        long authorId,
        List<Long> likesIds,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}