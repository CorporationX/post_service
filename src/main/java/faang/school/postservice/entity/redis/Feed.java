package faang.school.postservice.entity.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("feed")
public class Feed implements Serializable {

    @Id
    private long userId;
    private Set<Long> posts = new LinkedHashSet<>();
    @TimeToLive
    private Long ttl;
}
