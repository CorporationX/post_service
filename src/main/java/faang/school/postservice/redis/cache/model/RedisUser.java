package faang.school.postservice.redis.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Author")
public class RedisUser {
    @TimeToLive
    private int timeToLive;
    @Id
    private Long userId;
    List<Long> postsSubscribedTo;
}
