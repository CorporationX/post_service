package faang.school.postservice.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "author", timeToLive = 86400)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCache {
    private Long id;
    private String username;
    private String email;
}
