package faang.school.postservice.dto.redis;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CommentEventDto {
    private long postId;
    private Long authorId;
    private Long commentId;
    private LocalDateTime receivedAt;
}
