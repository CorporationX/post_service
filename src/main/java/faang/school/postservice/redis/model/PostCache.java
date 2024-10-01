package faang.school.postservice.redis.model;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.Deque;

@RedisHash(value = "post", timeToLive = 86400)
@Data
public class PostCache implements Serializable {
    @Value("spring.data.redis.post-cache.comments-in-post:3")
    private int maxCommentsQuantity;

    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer likes;
    private Integer views;
    private Deque<CommentDto> comments = new ArrayDeque<>(maxCommentsQuantity);

    public void addComment(CommentDto commentDto) {
        if (comments.size() == maxCommentsQuantity) {
            comments.removeLast();
        }
        comments.addFirst(commentDto);
    }
}