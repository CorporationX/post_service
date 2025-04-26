package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentEvent {
    private long postId;
    private long authorId;
    private long commentId;
    private LocalDateTime timestamp;
}
