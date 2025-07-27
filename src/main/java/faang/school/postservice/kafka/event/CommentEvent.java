package faang.school.postservice.kafka.event;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentEvent {
    private Long id;
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime timestamp;
}
