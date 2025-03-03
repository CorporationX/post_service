package faang.school.postservice.model.feed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "feed", timeToLive = 86400)
public class FeedCache implements Serializable {

    private Long userId;
    private RedisZSet<Long> postIds;
}
