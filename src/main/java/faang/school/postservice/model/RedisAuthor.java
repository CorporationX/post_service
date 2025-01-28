package faang.school.postservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "Author", timeToLive = 86400)
@Data
public class RedisAuthor {
    @Id
    private Long id;

    private String name;
}
