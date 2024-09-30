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
    private Long likes;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long views;
    @Version
    private Long version = 0L;

    public void incrementLike() {
        this.likes += 1;
        this.version++;
    }

    public void incrementViews() {
        this.views += 1;
        this.version++;
    }
}