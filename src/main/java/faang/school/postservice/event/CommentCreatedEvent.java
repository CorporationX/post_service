package faang.school.postservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CommentCreatedEvent {
    private Long commentId;
    private Long postId;
    private Long authorId;
}
