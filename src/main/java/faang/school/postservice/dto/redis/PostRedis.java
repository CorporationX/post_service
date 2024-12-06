package faang.school.postservice.dto.redis;

import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@Data
@Builder
@RedisHash(value = "posts", timeToLive = 86400)
public class PostRedis {
    @Id
    private Long postId;
    private Long authorId;
    private Long countLikes;
    private Long countComments;
    private Long countViews;
}
