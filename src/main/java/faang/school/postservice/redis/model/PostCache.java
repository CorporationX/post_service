package faang.school.postservice.redis.model;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "post", timeToLive = 86400)
@Data
@Builder
public class PostCache implements Serializable {
    @Value("spring.data.redis.post-cache.comments-in-post:3")
    private int maxCommentsQuantity;

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private Integer views;
    private final List<CommentDto> comments;

    public void addComment(CommentDto commentDto) {
        if (comments.size() == maxCommentsQuantity) {
            comments.remove(comments.size() - 1);
        }
        comments.add(0, commentDto);
    }
}