package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentNewsFeedDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentLinkedQueue;

@Data
@Builder
@AllArgsConstructor
@RedisHash(value = "post", timeToLive = 86400)
public class CachedPost implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long likes;
    private Long views;
    private Boolean published;
    private LocalDateTime createdAt;
    private final ConcurrentLinkedQueue<CommentNewsFeedDto> comments;
}