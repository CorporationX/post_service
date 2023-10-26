package faang.school.postservice.dto.redis;

import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("PostView")
public class PostViewCounter {
    @Id
    private long id;
    private long postId;
    private long views;
}
