package faang.school.postservice.model.cache;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash(value = "User", timeToLive = 86400)
public class User implements Serializable {

    @Id
    private Long id;
    private String username;
}
