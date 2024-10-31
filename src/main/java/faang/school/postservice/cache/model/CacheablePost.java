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
public class CacheablePost implements Serializable, Comparable<CacheablePost> {
    @Id
    private Long id;
    private String content;
    private CacheableUser author;
    private TreeSet<CacheableComment> comments;
    private LocalDateTime publishedAt;
    private long likesCount;
    private long views;

    @Override
    public int compareTo(CacheablePost other) {
        return other.id.compareTo(this.id);
    }
}
