package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostForCache implements Serializable {

    private long id;

    private String content;

    private Long authorId;

    private List<Long> lastCommentIds;

    private int commentsAmount;

    private long likesAmount;

    private LocalDateTime publishedAt;
}
