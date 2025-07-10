package faang.school.postservice.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentEventDto(
        Long postId,
        Long authorId,
        Long commentId,
        LocalDateTime dateTime
) {
}
