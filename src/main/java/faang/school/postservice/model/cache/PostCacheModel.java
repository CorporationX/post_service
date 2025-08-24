package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("${app.cache.post.key-prefix}")
public class PostCacheModel {
    @Id
    private String key;
    @TimeToLive
    private Long ttl;
    private Long id;
    private Long likeCount;
    private Long viewCount;
}
