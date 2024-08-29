package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LikeDto {
    private long id;
    private Long userId;
    private Long commentId;
    private Long postId;
}
