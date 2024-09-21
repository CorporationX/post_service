package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash(value = "post", timeToLive = 86400L)
public class PostInRedis implements Serializable {
    private String id;
    private String content;
    private Long authorId;
    private Long projectId;
}
