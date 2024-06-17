package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class LikeEvent {
    private Long postId;
    private Long postAuthorId;
    private Long likeUserId;
    private LocalDateTime dateTime;
}
