package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@RedisHash(value = "Feeds")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisFeed implements Serializable {

    @Id
    private long userId;
    private LinkedHashSet<RedisPost> feed;
}
