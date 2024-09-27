package faang.school.postservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.support.collections.RedisZSet;

import java.io.Serializable;


@RedisHash("Feed")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FeedRedis implements Serializable {
    private Long id;
    private RedisZSet<Long> postIds;
}
