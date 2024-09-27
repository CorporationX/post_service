package faang.school.postservice.redis.model;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "comment", timeToLive = 86400)
@Data
public class CommentCache {
    @Id
    private Long id;
    @Size(max = 4500, message = "Comment should be max 1000 characters")
    private String content;
    private Long authorId;
    private Long postId;
}