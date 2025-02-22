package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class NotificationCommentEvent implements Event {
    private long postId;
    private long authorId;
    private long postAuthorId;
    private long commentId;
    private String content;
}
