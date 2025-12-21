package faang.school.postservice.config.redis;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("author")
public record AuthorCache (
        @Id
        Long id,
        String username,
        String email,
        Boolean active
){
}
