package faang.school.postservice.redis.model;

import faang.school.postservice.kafka.model.CommentEvent;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@RedisHash(value = "posts", timeToLive = 86400)
@Data
public class PostCache {

    @Id
    private Long id;
    private Long authorId;
    private String content;
    private Integer likes;
    private Integer views;
    private List<CommentEvent> comments;
}
