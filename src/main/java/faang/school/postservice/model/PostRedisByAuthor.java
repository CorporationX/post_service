package faang.school.postservice.model;

import faang.school.postservice.dto.post.PostRedisDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@RedisHash(value = "postRedist", timeToLive = 60000000)
public class PostRedisByAuthor {
    @Id
    private Long authorId;

    private List<PostRedisDto> post;
}
