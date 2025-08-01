package faang.school.postservice.newsfeed.events;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostPublishEvent {
    Long authorId;
    Long postId;
    List<Long> subscriberIds;
}
