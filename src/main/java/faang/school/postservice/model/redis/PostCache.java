package faang.school.postservice.model.redis;

import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostCashed")
public class PostCache implements Serializable {
    @Id
    private long id;
    private long authorId;
    private String content;
    private LocalDateTime publishedAt;
    private AtomicLong likes;
    private TreeSet<CommentDto> comments;
    @TimeToLive
    @Value("${ttl.cache}")
    private long ttl;

    public PostCache(Post post) {
        this.id = post.getId();
        this.authorId = post.getAuthorId();
        this.content = post.getContent();
        this.publishedAt = post.getPublishedAt();
        this.likes = new AtomicLong(post.getLikes().size());
        this.comments = new TreeSet<>(Comparator.comparing(CommentDto::getCreatedAt));
    }
}
