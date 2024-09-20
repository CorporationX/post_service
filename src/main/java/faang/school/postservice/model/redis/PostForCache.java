package faang.school.postservice.model.redis;

import faang.school.postservice.model.Comment;
import faang.school.postservice.model.Like;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@RedisHash("Post")
public class PostForCache implements Serializable {

    private long id;

    private String content;

    private Long authorId;

    private List<Like> likes;

    private List<Comment> comments;

    private LocalDateTime publishedAt;
}
