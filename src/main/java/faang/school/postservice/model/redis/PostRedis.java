package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Posts")
public class PostRedis {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private AtomicLong likes;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private AtomicLong views;

    @Version
    private Long version;
}