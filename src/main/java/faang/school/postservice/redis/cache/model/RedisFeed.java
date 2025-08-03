package faang.school.postservice.redis.cache.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.TreeSet;

@Data
@RedisHash("Feed")
@AllArgsConstructor
@NoArgsConstructor
public class RedisFeed {
    @TimeToLive
    @JsonIgnore
    private int timeToLive;
    @Id
    private Long userId;
    private TreeSet<RedisPost> posts;
}
