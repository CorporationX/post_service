package faang.school.postservice.hash;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostHash")
public class PostHash {
   @Id
    private Long postId;

    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;

    @TimeToLive
    private Long ttl;

    @Version
    private long version;
}
