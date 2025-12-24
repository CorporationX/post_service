package faang.school.postservice.dto.post;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@RedisHash(value = "posts")
public class PostCache implements Serializable {
    @Id
    private String id;

    @Indexed
    private Long postId;

    private String content;
    private LocalDateTime publishedAt;
    private Long authorId;
    private Long projectId;
    private Long likeCount;
    private Long commentCount;

    @TimeToLive(unit = TimeUnit.SECONDS)
    private Long ttl;
}