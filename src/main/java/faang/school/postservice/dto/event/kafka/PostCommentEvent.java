package faang.school.postservice.dto.event.kafka;

import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.comment.CommentDto;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostCommentEvent {
    private long postId;
    private CommentCache comment;
}
