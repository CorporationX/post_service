package faang.school.postservice.dto.redis;

import faang.school.postservice.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("CachePost")
public class CachePost {
    private long id;
    private PostDto postDto;
    @TimeToLive
    private long ttl;
}
