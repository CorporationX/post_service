package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.List;

@RedisHash("posts")
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
