package faang.school.postservice.dto.kafkaevents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentEvent {
    private long id;
    private long commentAuthorId;
    private long postAuthorId;
    private long postId;
    private String content;
    private LocalDateTime createdAt;
}
