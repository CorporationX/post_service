package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Post;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("postCache")
public class PostCache implements Serializable {
    @Id
    private long id;
    private long authorId;
    private String content;
    private LocalDateTime publishedAt;
    private AtomicLong likes;
    private List<CommentDto> comments;

    public PostCache(Post post) {
        this.id = post.getId();
        this.authorId = post.getAuthorId();
        this.content = post.getContent();
        this.publishedAt = post.getPublishedAt();
        this.likes = new AtomicLong(post.getLikes().size());

    }
}
