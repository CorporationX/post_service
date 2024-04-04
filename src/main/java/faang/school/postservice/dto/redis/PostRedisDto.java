package faang.school.postservice.dto.redis;

import faang.school.postservice.model.Resource;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RedisHash(value = "Post")
@AllArgsConstructor
@NoArgsConstructor
public class PostRedisDto implements Serializable {
    @TimeToLive
    @Value("${spring.data.redis.cache.post.ttl}")
    private int ttl;
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long postViewCounter;
    private List<Resource> resources;
    private LocalDateTime publishedAt;

    public void postViewIncrement() {
        postViewCounter++;
    }
}