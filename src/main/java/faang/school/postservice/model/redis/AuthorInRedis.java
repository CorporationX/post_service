package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "AuthorInRedis")
public class AuthorInRedis implements Serializable {
    @Id
    private long id;
    private long postId;
    private long commentId;
    @Version
    private long version;
}
