package faang.school.postservice.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
@AllArgsConstructor
public class PostViewKafkaEvent implements KafkaEvent {

    private Long postId;
    private Long viewerId;
}
