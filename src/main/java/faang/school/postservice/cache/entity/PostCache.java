package faang.school.postservice.cache.entity;

import faang.school.postservice.dto.post.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("PostCache")
public class PostCache {
    @Id
    private Long id;

    private PostDto postDto;

    @TimeToLive
    private Long ttl;
}
