package faang.school.postservice.dto.redis;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeEventDto {
    private Long actorId;
    private Long receiverId;
    private LocalDateTime receivedAt;
}
