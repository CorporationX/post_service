package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "author", timeToLive = 86400)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorCache {
    @Id
    private Long id;
    private String username;
    private String email;
}