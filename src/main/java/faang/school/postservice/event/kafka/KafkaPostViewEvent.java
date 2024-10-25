package faang.school.postservice.event.kafka;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class KafkaPostViewEvent implements Serializable {
    private long postId;
    private long userId;
}
