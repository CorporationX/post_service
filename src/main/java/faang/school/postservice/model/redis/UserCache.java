package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("User")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCache implements Serializable {
    Long id;
    String username;
    String email;

    @TimeToLive
    private Long ttl;
}
