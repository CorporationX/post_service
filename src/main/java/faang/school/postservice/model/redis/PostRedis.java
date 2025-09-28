package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@RedisHash("posts")
public class PostRedis implements Serializable {
    @Id
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private long likes;
    private long views;
    @Transient
    private List<CommentDto> comments;
    private LocalDateTime createdAt;
    private LocalDateTime publishedAt;
    @TimeToLive
    private Long ttl;
}
