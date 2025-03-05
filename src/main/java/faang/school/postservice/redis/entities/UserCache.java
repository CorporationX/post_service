package faang.school.postservice.redis.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("users")
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {
    @Id
    private Long id;
    private String username;

    @TimeToLive(unit = TimeUnit.HOURS)
    final private Long ttl = 24L;
}