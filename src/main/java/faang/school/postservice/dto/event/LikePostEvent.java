package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikePostEvent {
    private long postId;
    private long authorPostId;
    private long userId;
    private LocalDateTime localDateTime;
}
