package faang.school.postservice.dto.hash;

import faang.school.postservice.dto.event_broker.CommentEvent;
import faang.school.postservice.dto.event_broker.LikePostEvent;
import faang.school.postservice.dto.event_broker.PostViewEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private Set<LikePostEvent> likes = new LinkedHashSet<>();
    private Set<PostViewEvent> views = new LinkedHashSet<>();
    private Set<CommentEvent> comments = new LinkedHashSet<>();

    @TimeToLive
    private Long ttl;

    @Version
    private Long version;
}
