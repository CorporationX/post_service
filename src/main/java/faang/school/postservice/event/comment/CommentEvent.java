package faang.school.postservice.event.comment;

import faang.school.postservice.event.Event;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentEvent implements Event {
    private long postId;
    private long authorId;
    private long commentId;
    private LocalDateTime timestamp = LocalDateTime.now();
}
