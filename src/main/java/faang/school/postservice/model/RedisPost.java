package faang.school.postservice.model;

import faang.school.postservice.dto.post.PostCacheDto;
import org.springframework.data.annotation.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Posts")
public class RedisPost {

    @Id
    private Long postId;

    private PostCacheDto postCacheDto;

    @Version
    private Long version;
}
