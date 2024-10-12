package faang.school.postservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostViewEvent {
    private long postId;
    private long userId;
}
