package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

/**
 * @author Alexander Bulgakov
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@RedisHash("Authors")
public class Author implements Serializable {
    @TimeToLive
    private long expiration;
    private long id;
    private long postId;
    private long commentId;
}
