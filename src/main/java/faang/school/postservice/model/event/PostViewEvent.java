package faang.school.postservice.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostViewEvent(
        long postId,
        long authorPostId,
        long viewUserId,
        LocalDateTime viewTime
) {
}
