package faang.school.postservice.dto.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentEvent {
    private Long id;
    private long postId;
    private long authorId;
    private String content;
    private LocalDateTime createdAt;
}
