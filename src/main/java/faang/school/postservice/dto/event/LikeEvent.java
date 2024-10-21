package faang.school.postservice.dto.event;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record LikeEvent(long postAuthorId,
                        long likeAuthorId,
                        long postId,
                        LocalDateTime timestamp) {
}
