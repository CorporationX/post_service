package faang.school.postservice.model.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "user", timeToLive = 86400)
public class UserCache implements Serializable {

    private Long id;
    private String username;
}
