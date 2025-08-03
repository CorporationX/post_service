package faang.school.postservice.kafka.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostViewedEvent {
    private Long viewerId;
    private Long postId;
}
