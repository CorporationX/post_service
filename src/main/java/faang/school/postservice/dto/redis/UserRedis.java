package faang.school.postservice.dto.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@Data
@Builder
@RedisHash(value = "users", timeToLive = 86400)
public class UserRedis {
    @Id
    private Long id;
    private String username;
    private String email;
    private List<Long> followers;
}
