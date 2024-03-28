package faang.school.postservice.dto.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("feed")
public class FeedHash {
    @Id
    private Long id;

    private TreeSet<PostIdTime> postIds;

    @Version
    private Long version;

    public FeedHash(Long id, TreeSet<PostIdTime> postIds) {
        this.id = id;
        this.postIds = postIds;
    }
}
