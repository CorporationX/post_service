package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@RedisHash(value = "post", timeToLive = 86400L)
public class PostInRedis implements Serializable {
    private long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private AtomicLong numberOfLikes;
}
