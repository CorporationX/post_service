package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RedisHash
@AllArgsConstructor
@NoArgsConstructor
public class CachedPost implements Serializable {
    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private List<CachedComment> comments;
    private List<CachedLike> likes;
    private int views = 0;
    private int likesCount = 0;
}
