package faang.school.postservice.dto.redis;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.RedisTemplate;

@RedisHash("Like")
public class LikeDto {

    private RedisTemplate<String, Object> redisTemplate;

    @Id
    private long id;
    private long postId;
    private long count;

    public void increment() {
        count++;
    }
}
