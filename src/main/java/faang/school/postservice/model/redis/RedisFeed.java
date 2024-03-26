package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.TreeSet;

@RedisHash("Feeds")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RedisFeed implements Serializable {

    @Id
    private long userId;
    private TreeSet<RedisPost> posts;
}
