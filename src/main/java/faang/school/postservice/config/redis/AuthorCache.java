package faang.school.postservice.config.redis;

import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Builder
@RedisHash("author")
public record AuthorCache (
        @Id
        Long id,
        String username,
        String email,
        Boolean active,
        @TimeToLive
        Long ttl
){
}
