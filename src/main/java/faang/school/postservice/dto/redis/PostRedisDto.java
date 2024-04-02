package faang.school.postservice.dto.redis;

import faang.school.postservice.config.RedisConfig;
import faang.school.postservice.model.Resource;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@RedisHash(value = "Post")
public class PostRedisDto implements Serializable {
    public PostRedisDto(RedisConfig config) {
        this.ttl = config.getTtl();
    }

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private AtomicLong postViewCounter;
    private List<Resource> resources;
    private LocalDateTime publishedAt;

    @TimeToLive
    private int ttl;

    public void postViewIncrement() {
        postViewCounter.incrementAndGet();
    }
}
