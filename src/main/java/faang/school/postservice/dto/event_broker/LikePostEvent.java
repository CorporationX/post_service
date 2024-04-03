package faang.school.postservice.dto.event_broker;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class LikePostEvent {
    private Long postId;
    private Long userId;
    private LocalDateTime createdAt;
}