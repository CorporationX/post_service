package faang.school.postservice.dto.redis;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash("Post")
@Data
@Builder
public class PostRedisDto {

    @Id
    private Long id;
    private String content;
    private LinkedHashSet<CommentRedisDto> comments;
    private long likeCounterId;
    private long postViewCounterId;
    private LinkedHashSet<Long> followersIds;
}
