package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostRedisCache implements Serializable {

    @Id
    private Long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;

    private String content;
    private Long authorId;
    private Long projectId;
    private List<String> resourceIds;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
    private int likesCount;
}
