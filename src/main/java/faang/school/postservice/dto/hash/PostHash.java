package faang.school.postservice.dto.hash;

import faang.school.postservice.dto.post.PostViewEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "post")
public class PostHash {
    @Id
    private Long postId;
    private Long userAuthorId;
    private Long projectAuthorId;
    private String content;
    private LocalDateTime publishedAt;
    private List<PostViewEvent> views = new LinkedList<>();

    @TimeToLive
    private Long ttl;

    @Version
    private Long version;
}
