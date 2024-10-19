package faang.school.postservice.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Post")
@Builder
public class PostRedis implements Serializable, Comparable<PostRedis> {
    @Id
    private Long id;
    private String content;
    private UserRedis author;
    private TreeSet<CommentRedis> comments;
    private LocalDateTime publishedAt;
    private long likesCount;
    private long views;

    @Override
    public int compareTo(PostRedis other) {
        return other.id.compareTo(this.id);
    }
}
