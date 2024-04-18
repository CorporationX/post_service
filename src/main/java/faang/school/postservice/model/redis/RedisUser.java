package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("User")
public class RedisUser implements Serializable {
    @Id
    private Long id;
    @TimeToLive
    private int ttl;
    @Version
    private long version;

    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;
}