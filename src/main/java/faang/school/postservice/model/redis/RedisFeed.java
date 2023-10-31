package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Feed", timeToLive = 86400)
public class RedisFeed {
    @Id
    private Long userId;
    private LinkedHashSet<Long> postIds;
    @Version
    private Long version;
}
