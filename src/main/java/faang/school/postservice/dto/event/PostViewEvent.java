package faang.school.postservice.dto.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PostViewEvent {
    private long receiverId;
    private long actorId;
    private LocalDateTime receivedAt;
}
