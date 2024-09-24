package faang.school.postservice.dto.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentDto(
        Long id,

        String content,

        Long authorId,

        LocalDateTime updatedAt) {
}
