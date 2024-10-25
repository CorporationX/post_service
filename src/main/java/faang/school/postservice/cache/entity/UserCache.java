package faang.school.postservice.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("UserCache")
public class UserCache {
    @Id
    private Long id;

    private String username;
    private List<Long> userFollowers;
    private List<Long> userSubscribedAuthors;

    @TimeToLive
    private Long ttl;
}
