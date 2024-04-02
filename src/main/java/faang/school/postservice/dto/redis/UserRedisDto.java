package faang.school.postservice.dto.redis;

import faang.school.postservice.config.RedisConfig;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@RedisHash("User")
public class UserRedisDto implements Serializable {

    public UserRedisDto(RedisConfig config) {
        this.ttl = config.getTtl();
    }

    @Id
    private Long id;
    private String name;

    @TimeToLive
    private int ttl;
}
