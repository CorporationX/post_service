package faang.school.postservice.dto.redis;

import faang.school.postservice.model.Resource;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;

@RedisHash("Post")
@Data
@Builder
public class PostRedisDto {

    @Id
    private long id;
    private long authorId;
    private String content;
    private LinkedHashSet<CommentRedisDto> comments;
    private long likeCounter;
    private long postViewCounter;
    private List<Resource> resources;
    private LocalDateTime publishedAt;

    public void postViewIncrement(long views) {
        postViewCounter += views;
    }
}
