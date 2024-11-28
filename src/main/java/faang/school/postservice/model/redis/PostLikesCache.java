package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash("likes")
@Data
@Builder
public class PostLikesCache {

    @Id
    private Long id; // postId
    private LinkedHashSet<Long> likeIds;

    @Version
    private long version;
}
