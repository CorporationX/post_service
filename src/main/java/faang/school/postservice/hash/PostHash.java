package faang.school.postservice.hash;

import faang.school.postservice.dto.event.CommentEventKafka;
import faang.school.postservice.dto.event.LikeEventKafka;
import faang.school.postservice.dto.event.PostViewEventKafka;
import faang.school.postservice.model.Post;
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
    private LinkedHashSet<PostViewEventKafka> postViews;

    @TimeToLive
    private Long ttl;

    @Version
    private long version;

    public PostHash (Post post) {
        this.postId = post.getId();
        this.content = post.getContent();
        this.authorId = post.getAuthorId();
        this.projectId = post.getProjectId();
        this.publishedAt = post.getPublishedAt();
        this.comments = new LinkedHashSet<>();
        this.likes = new LinkedHashSet<>();
        this.postViews = new LinkedHashSet<>();
        this.ttl = 84600L;
        this.version = 1L;
    }
}
