package faang.school.postservice.model.redis;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RedisHash(value = "Posts")
public class PostRedis implements Serializable {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private Long countLikes;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    @TimeToLive
    @Transient
    @Value("${cache.post.live-time}")
    private long timeToLive;

}