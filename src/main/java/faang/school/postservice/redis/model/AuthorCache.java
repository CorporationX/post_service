package faang.school.postservice.redis.model;

import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "author", timeToLive = 86400)
@Data
public class AuthorCache {
    private Long id;
    private String username;
    private String email;
}
