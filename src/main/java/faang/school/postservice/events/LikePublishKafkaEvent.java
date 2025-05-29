package faang.school.postservice.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikePublishKafkaEvent {

    private long likeId;
    private long postId;
    private LikeEventType likeEventType;
}
