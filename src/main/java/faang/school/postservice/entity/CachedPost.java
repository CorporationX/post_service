package faang.school.postservice.entity;


import faang.school.postservice.dto.comment.CommentDto;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.data.relational.core.mapping.Embedded;

import java.time.Instant;
import java.util.List;

@RedisHash(value = "posts")
@Data
@Builder
public class CachedPost {

    @Id
    private Long id;
    private String content;
    @Indexed
    private Long authorId;

    @Embedded.Nullable
    @Indexed
    private Long projectId;
    private Instant publishedAt;

    private int views;
    private int likes;
    private List<CommentDto> comments;

}
