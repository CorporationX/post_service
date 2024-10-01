package faang.school.postservice.dto.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentCreatedEvent {
    private long commentId;
    private long authorId;
    private long postId;
}
