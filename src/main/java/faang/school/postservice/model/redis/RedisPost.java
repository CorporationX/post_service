package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Post")
public class RedisPost {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    //private List<RedisCommentDto> redisComments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    @TimeToLive
    private int ttl;
    @Version
    private long version;
}