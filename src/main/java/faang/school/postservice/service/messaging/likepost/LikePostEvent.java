package faang.school.postservice.service.messaging.likepost;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class LikePostEvent {
    private final Long likeAuthorId;
    private final Long postId;
    private final Long postAuthorId;
}
