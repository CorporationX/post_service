package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class CommentEventKafka {
    private long commentId;
    private long authorId;
    private long postId;
    private String content;
    LocalDateTime createdAt;
}
