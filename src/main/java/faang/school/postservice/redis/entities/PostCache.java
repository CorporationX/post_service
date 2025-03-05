package faang.school.postservice.redis.entities;

import faang.school.postservice.dto.post.PostVisibility;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostCache implements Serializable {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    @Builder.Default
    private Long likesCount = 0L;
    @Builder.Default
    private Long commentsCount = 0L;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime publishedAt;
    private boolean verified;
    private boolean published;
    private PostVisibility visibility;
    @Builder.Default
    private LinkedHashSet<CommentCache> lastComments = new LinkedHashSet<>();

    @TimeToLive(unit = TimeUnit.HOURS)
    final private Long ttl = 24L;
}