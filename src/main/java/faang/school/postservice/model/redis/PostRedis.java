package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.PostInRedisDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "PostInRedis", timeToLive = 86400L)
public class PostRedis implements Serializable {

    @Id
    private String id;
    PostInRedisDto post;
}
