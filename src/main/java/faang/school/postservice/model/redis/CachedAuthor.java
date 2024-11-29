package faang.school.postservice.model.redis;


import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "author", timeToLive = 86400)
public class CachedAuthor implements Serializable {
    @Id
    private Long id;
    private String username;
    private String email;
}
