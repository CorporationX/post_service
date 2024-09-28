package faang.school.postservice.model.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class KafkaCommentEvent implements Serializable {

    private long postId;
    private long commentAuthorId;
}
