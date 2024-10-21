package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@RedisHash(value = "feed", timeToLive = 86400)
@AllArgsConstructor
public class Feed {
    @Id
    private Long userId;
    private List<PostCache> posts;
}
