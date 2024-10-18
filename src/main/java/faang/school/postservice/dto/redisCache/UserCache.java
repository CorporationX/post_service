package faang.school.postservice.dto.redisCache;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "Users")
@Data
@AllArgsConstructor
public class UserCache {
    @Id
    private long id;
    private String username;

    @TimeToLive
//    @Value("${cache.post.ttl:84600}")
    private Long ttl;
}
