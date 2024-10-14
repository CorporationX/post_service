package faang.school.postservice.event.kafka;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KafkaPostViewEvent {
    private long postId;
    private long userId;
}
