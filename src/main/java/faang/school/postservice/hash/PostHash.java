package faang.school.postservice.hash;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
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
import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostHash")
public class PostHash implements Serializable {

    @Id
    private Long postId;

    private String content;
    private Long authorId;
    private Long projectId;
    private LocalDateTime publishedAt;
    private LinkedHashSet<CommentEventKafka> comments;
    private LinkedHashSet<LikeEventKafka> likes;

    @TimeToLive
    private Long ttl;

    @Version
    private long version;
}
