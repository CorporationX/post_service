package faang.school.postservice.model.cache;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@RedisHash("CachePost")
public class CachePost implements Serializable {
    @Id
    private String id;
    private String content;
    private Long authorId;
}
