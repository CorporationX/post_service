package faang.school.postservice.event.kafka;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class KafkaPostEvent {
    private long postId;
    private long authorId;
    private List<Long> subscribersId;
}
