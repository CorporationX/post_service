package faang.school.postservice.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEventDto(
    Long authorId,
    Long senderId,
    Long postId,
    Long commentId,
    LocalDateTime date
) {
}
