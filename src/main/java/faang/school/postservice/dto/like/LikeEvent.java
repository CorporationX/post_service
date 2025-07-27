package faang.school.postservice.dto.like;

import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public record LikeEvent(String eventType,
        Long userId,
        Long postId,
        Long commentId, LocalDateTime timestamp) {

}
