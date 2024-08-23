package faang.school.postservice.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeEvent {
    private Long postId;
    private Long authorId;
    private Long userId;
}
