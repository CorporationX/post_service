package faang.school.postservice.dto.post;


import lombok.*;
import org.springframework.data.redis.core.RedisHash;



import java.io.Serializable;



@RedisHash(value = "Post")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedPostDto implements Serializable {
    private Long id;
    private Long authorId;
    private Long projectId;
    private String content;
    private long likesCount;
    private long commentsCount;
}
