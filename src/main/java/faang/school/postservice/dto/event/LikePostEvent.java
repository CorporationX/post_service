package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class LikePostEvent {
    private Long postAuthorId;
    private Long likeAuthorId;
    private long postId;
}
