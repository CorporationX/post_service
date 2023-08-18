package faang.school.postservice.dto.redis;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostViewEventDto {
    private Long userId;
    private Long postId;
    private Long authorId;
    private LocalDateTime createdAt;
}
