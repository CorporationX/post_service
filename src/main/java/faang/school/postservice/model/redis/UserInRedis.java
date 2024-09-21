package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "user", timeToLive = 86400L)
public class UserInRedis {
    private String id;
    private String username;
    private String email;
}
