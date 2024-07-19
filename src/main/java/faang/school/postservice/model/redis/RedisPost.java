package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("posts")
public class RedisPost implements Serializable {
    @Id
    private Long id;
    @TimeToLive
    private int ttl;

    private String content;
    private Long authorId;
    private SortedSet<RedisCommentDto> comments = new TreeSet<>();
    private Set<Long> likedUserIds = new HashSet<>();
    private Set<Long> viewedUserIds = new HashSet<>();
    private LocalDateTime publishedAt;
    @Version
    private LocalDateTime updatedAt;
}
