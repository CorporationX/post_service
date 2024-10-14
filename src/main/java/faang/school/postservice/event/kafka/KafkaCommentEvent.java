package faang.school.postservice.event.kafka;

import lombok.Data;

@Data
public class KafkaCommentEvent {
    private long id;
    private long authorId;
    private long postId;
}
