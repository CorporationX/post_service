package faang.school.postservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LikeEvent {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
}