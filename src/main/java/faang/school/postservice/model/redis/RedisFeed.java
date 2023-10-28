package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.TimePostId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.util.SortedSet;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Feed")
public class RedisFeed {

    @Id
    private Long userId;
    private SortedSet<TimePostId> postsId;
    @Version
    private long version;
}
