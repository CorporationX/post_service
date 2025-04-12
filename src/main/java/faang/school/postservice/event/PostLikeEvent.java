package faang.school.postservice.event;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLikeEvent {
    private Long postAuthorId;
    private Long likeAuthorId;
    private Long postId;
}
