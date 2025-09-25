package faang.school.postservice.redis;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@RedisHash("Comment")
public record CommentCache(
    Long id,
    Long authorId,
    String content,
    LocalDateTime createdAt) implements Serializable {
}