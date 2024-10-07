package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentEvent implements Serializable {
    private long commentId;
    private long authorId;
    private long postId;
    private LocalDateTime date;
}
