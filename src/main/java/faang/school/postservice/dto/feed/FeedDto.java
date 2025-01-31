package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@RedisHash("Feed")
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto implements Serializable {

    @Id
    private String userId;
    private Set<Long> postIds;

    @TimeToLive(unit = TimeUnit.DAYS)
    private Long ttl;
}
