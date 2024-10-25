package faang.school.postservice.event.kafka;

import lombok.Data;

import java.io.Serializable;

@Data
public class KafkaCommentEvent implements Serializable {
    private long id;
    private long postId;
    private long authorId;
}
