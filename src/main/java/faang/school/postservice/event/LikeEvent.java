package faang.school.postservice.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEvent(
        long postId,
        long userId,
        LocalDateTime likedTime
) {
}
