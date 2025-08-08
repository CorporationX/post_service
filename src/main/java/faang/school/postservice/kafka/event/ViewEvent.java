package faang.school.postservice.kafka.event;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ViewEvent {
    private UUID id;
    private Long postId;
    private Long userId;
    private LocalDateTime timestamp;
}
