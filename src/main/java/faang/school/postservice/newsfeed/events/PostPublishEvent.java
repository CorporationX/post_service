package faang.school.postservice.newsfeed.events;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PostPublishEvent {
    Long authorId;
    Long postId;
    List<Long> subscriberIds;
    }
