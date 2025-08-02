package faang.school.postservice.model.redis;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CachedComment {
    private Long id;
    private Long authorId;
    private Long postId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
