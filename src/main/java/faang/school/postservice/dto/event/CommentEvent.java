package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentEvent {
    long authorCommentId;
    long authorPostId;
    long postId;
    long commentId;
    String commentText;
}
