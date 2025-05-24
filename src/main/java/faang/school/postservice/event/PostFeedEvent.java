package faang.school.postservice.event;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PostFeedEvent {
    private Long postId;
    private List<Long> subscriberIds;
    private LocalDateTime publishedAt;
}