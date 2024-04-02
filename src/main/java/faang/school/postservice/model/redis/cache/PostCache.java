package faang.school.postservice.model.redis.cache;

import faang.school.postservice.model.Post;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("PostCache")
public class PostCache implements Serializable {

    @Id
    private long id;
    private long authorId;
    private String content;
    private LocalDateTime publishedAt;
    @TimeToLive
    @Value("${spring.data.redis.post.ttl}")
    private int postsTtl;

    public PostCache(Post post) {
        this.id = post.getId();
        this.authorId = post.getAuthorId();
        this.content = post.getContent();
        this.publishedAt = post.getPublishedAt();
    }

}