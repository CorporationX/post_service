package faang.school.postservice.model.cache;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@RedisHash(value = "Post", timeToLive = 86400)
public class Post implements Serializable {

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private int likesCount;
    private int commentsCount;
    private int viewsCount;
    LocalDateTime publishedAt;
    LocalDateTime updatedAt;
    @Version
    private int version;
}
