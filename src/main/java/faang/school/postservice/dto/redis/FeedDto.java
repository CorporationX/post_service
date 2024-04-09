package faang.school.postservice.dto.redis;

import faang.school.postservice.config.RedisConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.LinkedHashSet;

@Data
@Builder
@RedisHash(value = "Feed")
@AllArgsConstructor
@NoArgsConstructor
public class FeedDto {
    public FeedDto(RedisConfig config) {
        this.ttl = config.getTtl();
    }

    private UserRedisDto userRedisDto;
    private final LinkedHashSet<PostRedisDto> posts = new LinkedHashSet<>(500);

    @TimeToLive
    private int ttl;
}
