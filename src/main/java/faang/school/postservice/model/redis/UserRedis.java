package faang.school.postservice.model.redis;

import faang.school.postservice.model.OwnerType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@RedisHash("users")
public class UserRedis {
    @Id
    private long id;
    private String username;
    private OwnerType ownerType;
    @TimeToLive
    private Long ttl;
}
