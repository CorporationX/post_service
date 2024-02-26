package faang.school.postservice.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("feed")
public class FeedHash implements Serializable {
    @Id
    private Long id;

    private Set<Long> postIds = new LinkedHashSet<>();

    @Version
    private Long version;

    public FeedHash(long id, Set<Long> postIds) {
        this.id = id;
        this.postIds = postIds;
    }
}
