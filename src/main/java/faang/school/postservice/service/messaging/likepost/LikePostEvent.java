package faang.school.postservice.service.messaging.likepost;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class LikePostEvent {
    private final Long likeAuthorId;
    private final Long postId;
    private final Long postAuthorId;
    private final LocalDateTime timestamp;
}