package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash("post_comments")
@Data
@Builder
public class PostCommentsCache {

    @Id
    private Long id; // postId
    private LinkedHashSet<Long> commentIds;

    @Version
    private long version;
}
