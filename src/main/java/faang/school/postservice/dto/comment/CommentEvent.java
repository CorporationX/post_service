package faang.school.postservice.dto.comment;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentEvent {
    private long commentAuthorId;
    private long postAuthorId;
    private long commentId;
    private long postId;
    private String content;
}