package faang.school.postservice.dto.comment;

import java.time.LocalDateTime;
import java.util.Date;

public record CommentEvent(long commentId, long authorId, long postId, LocalDateTime date) {
}
