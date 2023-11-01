package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@RedisHash(value = "Posts", timeToLive = 86400)
public class RedisPost implements Serializable {

    @Id
    private Long postId;
    private String content;
    private Long authorId;
    private Long postViews;
    private Long postLikes;
    private List<RedisCommentDto> commentsDto;
    @Version
    private int version; // нету в мапере
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;

    public synchronized void incrementPostView() {
        postViews++;
    }

    public synchronized void incrementPostLike() {
        postLikes++;
    }

    public synchronized void incrementPostVersion() {
        version++;
    }
}
