package faang.school.postservice.model.cache;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@RedisHash(value = "user", timeToLive = 86400)
public class User implements Serializable {

    @Id
    private Long id;
    private String username;
}
