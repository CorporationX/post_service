package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
    private Long likeId;
    private Long postId;
    private Long commentId;
    private Long userId;
}