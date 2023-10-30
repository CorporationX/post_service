package faang.school.postservice.dto.redis;

import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

@Builder
@RedisHash("Comment")
public class CommentRedisDto {
    private Long id;
    private String content;
    private Long authorId;
    private Long postId;
}
