package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.RedisCommentDto;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayDeque;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "Post", timeToLive = 86400)
public class RedisPost implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long postViews;
    private Integer postLikes;
    private ArrayDeque<RedisCommentDto> comments;
    @Version
    private Long version;
    private LocalDateTime publishedAt;
    private LocalDateTime updatedAt;
}
