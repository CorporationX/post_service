package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@RedisHash("Post")
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private String content;
    private UserRedis author;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private long likesCount;
}
