package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@RedisHash("post")
@Data
public class RedisPost {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private Integer likes;
    private List<RedisComment> comments;
    @TimeToLive
    private Long timeToLeave;
}
