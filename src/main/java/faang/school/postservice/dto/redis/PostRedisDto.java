package faang.school.postservice.dto.redis;

import faang.school.postservice.model.Resource;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@RedisHash("Post")
public class PostRedisDto {

    @Id
    private Long id;
    private String content;
    private long postViewCounter;
    private List<Resource> resources;
    private LocalDateTime publishedAt;

    public void postViewIncrement() {
        postViewCounter++;
    }
}
