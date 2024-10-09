package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash("feed")
@Data
@AllArgsConstructor
public class NewsFeedRedis {
    @Id
    private Long userId;
    private List<Long> posts;

}
