package faang.school.postservice.dto.feed.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
@Builder
@RedisHash("User")
@AllArgsConstructor
@NoArgsConstructor
public class UserRedisDto implements Serializable {
    @Id
    private Long id;
    private String name;
    @TimeToLive
    @Value("${spring.data.redis.cache.ttl.user}")
    private int ttl;
}