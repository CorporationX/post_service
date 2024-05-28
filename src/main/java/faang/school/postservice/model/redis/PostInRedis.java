package faang.school.postservice.model.redis;

import faang.school.postservice.dto.post.PostInRedisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "PostInRedis")
public class PostInRedis implements Serializable {
    @Id
    private long id;
    @Indexed
    PostInRedisDto post;
}
