package faang.school.postservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedUpdateEvent {
    private Long postId;
    private Long authorId;
    private Long projectId;
    private List<Long> subscriberIds;
}