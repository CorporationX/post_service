package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@RedisHash(value = "authors", timeToLive = 86400)
public class AuthorCache {
    @Id
    private Long id;
    private String username;
    private String email;
    private Long postId;
    private List<Long> subscribers;
}
