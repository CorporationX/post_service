package faang.school.postservice.dto.kafka_events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewKafkaEvent {
    private Long id;
    private Long viewerId;
    private Long postId;
}
