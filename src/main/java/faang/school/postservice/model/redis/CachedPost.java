package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RedisHash("post")
public class CachedPost implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<CachedLike> likes;
    private List<CachedComment> comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private int views;
}
