package faang.school.postservice.dto.redis;

import faang.school.postservice.config.RedisConfig;
import jakarta.persistence.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash("User")
public class UserRedisDto {

    public UserRedisDto(RedisConfig config) {
        this.ttl = config.getTtl();
    }

    @Id
    private Long id;
    private String name;
    @TimeToLive
    private long ttl;
}
