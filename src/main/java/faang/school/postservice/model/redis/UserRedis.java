package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
@AllArgsConstructor
@Data
public class UserRedis {
    @Id
    private Long id;
    private String username;
}
