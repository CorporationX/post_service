package faang.school.postservice.model.redis;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash("feed")
@Data
@Builder
public class FeedCache {

    @Id
    private Long id; // userId
    //private Long userId; - спринга ругается, если @Id называется не id
    private LinkedHashSet<Long> postIds;

    @Version
    private Long version;
}
