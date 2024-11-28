package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.HashSet;

@RedisHash("post_views") // TODO заменить на параметр из application.yaml
@Builder
@Data
public class PostViewsCache {

    @Id
    private long id; // postId
    private HashSet<Long> userIds;

    @Version
    private long version;
}
