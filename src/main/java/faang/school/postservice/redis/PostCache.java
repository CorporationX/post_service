package faang.school.postservice.redis;

import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Post")
public record PostCache(
    Long id,
    String content,
    Long authorId,
    Long projectId,
    List<CommentCache> lastComments,
    LocalDateTime publishedAt,
    @TimeToLive
    Long ttl) implements Serializable {
}