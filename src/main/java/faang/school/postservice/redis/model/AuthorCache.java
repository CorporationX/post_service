package faang.school.postservice.redis.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "author", timeToLive = 86400)
public class AuthorCache {
    @Id
    private Long id;
    private String username;
    private String email;
}