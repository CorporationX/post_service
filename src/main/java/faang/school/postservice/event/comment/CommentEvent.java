package faang.school.postservice.event.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent {
    private long id;
    private long authorId;
    private long postId;
    private String content;
}
