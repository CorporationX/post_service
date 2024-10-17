package faang.school.postservice.model.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentEvent {
    private Long authorId;
    private Long postAuthorId;
    private Long postId;
    private String postText;
    private Long commentId;
}
