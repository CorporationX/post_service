package faang.school.postservice.model.redis;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("Feed")
public class RedisFeed {
    private Long id;
    private Long userId;
    private List<Long> postIds;
}
