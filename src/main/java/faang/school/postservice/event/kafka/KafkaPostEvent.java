package faang.school.postservice.event.kafka;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class KafkaPostEvent implements Serializable {
    private long postId;
    private List<Long> subscribersId;
}
