package faang.school.postservice.redis.model;

import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@RedisHash(value = "posts", timeToLive = 86400)
@Data
@Builder
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer views;
    private List<CommentDto> comments;
}
