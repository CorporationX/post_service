package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("cacheCommentAuthor")
public class CacheCommentAuthor {
    @Id
    private long id;
    private String userName;

    @TimeToLive
    private long ttl;
}
