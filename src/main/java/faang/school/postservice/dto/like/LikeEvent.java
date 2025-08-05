package faang.school.postservice.dto.like;

import java.time.LocalDateTime;

public record LikeEvent(String eventType,
                        Long userId,
                        Long postId,
                        Long commentId, LocalDateTime timestamp) {

}
