package faang.school.postservice.config.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@RedisHash(value = "Post", timeToLive = 86400)
@AllArgsConstructor
public class PostRedis implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long likeCount;
}
