package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

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
    //private List<RedisCommentDto> redisComments;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
    @TimeToLive
    @Value("${spring.data.redis.cache.ttl.post}")
    private int ttl;
    @Version
    private long version;
}