package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
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
