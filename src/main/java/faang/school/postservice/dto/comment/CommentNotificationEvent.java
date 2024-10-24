package faang.school.postservice.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentNotificationEvent {
    private Long postId;
    private Long commentId;
    private Long authorPostId;
    private Long authorCommentId;
    private String content;
}
