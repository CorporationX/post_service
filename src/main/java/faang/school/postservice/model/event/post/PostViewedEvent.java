package faang.school.postservice.model.event.post;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostViewedEvent {
    private Long postId;
    private Long authorId;
    private Long viewerId;
    private LocalDateTime timestamp;
    private PostEventType type;
}
