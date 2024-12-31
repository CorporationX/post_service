package faang.school.postservice.redis.model;


import faang.school.postservice.dto.comment.CommentDto;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@RedisHash(value = "posts", timeToLive = 86400)
public class PostCache implements Serializable {
    @Id
    private Long id;
    private String content;
    private Long authorId;
    private Integer views;
    private List<CommentDto> comments;
}