package faang.school.postservice.model.feed;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArraySet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("posts")
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Long projectId;
    private boolean published;

    private Long likes;
    private Long views;

    private LocalDateTime publishedAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private CopyOnWriteArraySet<CommentDto> comments;
}