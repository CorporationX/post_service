package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@RedisHash(value = "like")
public class LikeRedis {
    @Id
    private Long id;
    private Long userId;
    private Long commentId;
    private Long postId;
    private LocalDateTime createdAt;
}
