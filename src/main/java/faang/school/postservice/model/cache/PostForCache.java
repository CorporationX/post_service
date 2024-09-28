package faang.school.postservice.model.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.TreeSet;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostForCache implements Serializable {

    private long id;

    private String content;

    private Long authorId;

    private TreeSet<Long> lastCommentIds = new TreeSet<>();

    private int commentsAmount;

    private long likesAmount;

    private LocalDateTime publishedAt;
}
