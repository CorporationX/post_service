package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Post", timeToLive = 86400L)
public class RedisPost {

    @Id
    private Long id;
    private String content;
    private Long userId;
    private int likes;
    private List<RedisCommentDto> redisComments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    @Version
    private long version;

    public void likeIncrement() {
        likes++;
    }

    public void likeDecrement() {
        likes--;
    }
}
