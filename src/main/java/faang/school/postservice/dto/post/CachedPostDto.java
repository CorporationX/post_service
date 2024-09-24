package faang.school.postservice.dto.post;


import jakarta.persistence.Version;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;


import java.io.Serializable;


@RedisHash(value = "Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedPostDto implements Serializable {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private long likesCount;
    private long commentsCount;

    @Version
    private Long version = 0L;

    public void incrementLikesCount() {
        likesCount += 1;
        version++;
    }

    private void incrementCommentsCount() {
        commentsCount++;
        version++;
    }
}
