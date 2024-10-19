package faang.school.postservice.cache.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("User")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserRedis {
    @Id
    private Long id;
    private String username;
}
