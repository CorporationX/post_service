package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentEvent extends Event {

    private long postId;

    private long postAuthorId;

    private long authorId;

    private long commentId;

    private String commentContent;

    private LocalDateTime sendAt;
}
