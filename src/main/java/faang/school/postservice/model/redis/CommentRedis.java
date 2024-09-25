package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class CommentRedis {
    private Long postId;
    private Long authorId;
    private String content;
    private LocalDateTime createdAt;
}
