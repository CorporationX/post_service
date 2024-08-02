package faang.school.postservice.dto.like;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LikeEvent {
    private Long id;
    private Long postId;
    private Long commentId;
    private Long userId;
}
