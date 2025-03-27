package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Data
@RedisHash("comments")
public class RedisComment {
    private String content;
    private Long authorId;
    private Integer likes;
}
