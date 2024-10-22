package faang.school.postservice.model.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeEvent {
    private Long postId;
    private Long userId;
    private Long authorId;
    private LocalDateTime createdAt;
}
