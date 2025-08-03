package faang.school.postservice.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCommentedEvent {
    private Long postId;
    private Long authorId;
    private Long commentId;
}
