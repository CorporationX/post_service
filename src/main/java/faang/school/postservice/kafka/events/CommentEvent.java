package faang.school.postservice.kafka.events;

import faang.school.postservice.kafka.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CommentEvent extends Event {
    private Long postId;
    private Long authorId;
}
