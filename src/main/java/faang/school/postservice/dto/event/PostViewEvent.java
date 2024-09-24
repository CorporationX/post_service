package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostViewEvent {
    private long postId;
    private long userId;
    private LocalDateTime viewedAt;
}
