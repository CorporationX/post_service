package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikePostEvent {
    private final long id;
    private final long postId;
    private final long userId;
}
