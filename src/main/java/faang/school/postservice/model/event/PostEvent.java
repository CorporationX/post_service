package faang.school.postservice.model.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostEvent(
        long postId,
        long authorPostId,
        long viewUserId,
        LocalDateTime viewTime
) {
}
