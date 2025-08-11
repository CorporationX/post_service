package faang.school.postservice.kafka.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LikeEvent {
    private Long id;
    private Long postId;
    private Long authorId;
    private LocalDateTime timestamp;
}
