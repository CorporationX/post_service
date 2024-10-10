package faang.school.postservice.model.dto.like;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEventDto(
        long postId,
        long userId,
        LocalDateTime likedTime
) {
}
