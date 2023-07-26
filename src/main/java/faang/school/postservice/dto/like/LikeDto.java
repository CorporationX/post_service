package faang.school.postservice.dto.like;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LikeDto {
    private Long id;
    private Long userId;
    private Long postId;
    private Long commentId;
    private LocalDateTime createdAt;
}
