package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@RedisHash("Post")
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<LikeRedis> likes;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
