package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "posts", timeToLive = 86400L)
public class RedisPost {
    @Id
    @Getter
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private List<Long> likeIds;
    private List<Long> commentIds;
    private List<Long> albumIds;
    private Long adId;
    private List<Long> resourceIds;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private boolean deleted;
    private Long numLikes;
}
