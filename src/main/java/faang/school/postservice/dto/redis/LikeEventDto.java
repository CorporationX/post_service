package faang.school.postservice.dto.redis;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeEventDto {
    private Long actorId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}
