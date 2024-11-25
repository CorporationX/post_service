package faang.school.postservice.model.redis;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Post")
public class PostRedis implements Serializable {

    @Id
    private Long id;

    private Long authorId;

    private String content;

    private LocalDateTime publishedAt;

    private List<Long> likeIds;
    private List<Long> commentIds;

    @TimeToLive
    private Long ttl;
}
