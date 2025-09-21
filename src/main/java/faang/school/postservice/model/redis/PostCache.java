package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Post")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostCache implements Serializable {
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<CommentCache> lastComments;
    private LocalDateTime publishedAt;

    @TimeToLive
    private Long ttl;
}
