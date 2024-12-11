package faang.school.postservice.model.event.kafka;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.kafka.common.protocol.types.Field;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PostViewKafkaEvent {
    private Long postId;
    private Long viewerId;
    private String viewDateTime;
}
