package faang.school.postservice.model.redis;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@RedisHash("User")
@AllArgsConstructor
@NoArgsConstructor
public class RedisUser implements Serializable {
    @Id
    private Long id;
    private String username;
    private List<Long> followerIds;
    private List<Long> followeeIds;
    private String pictureFileId;
    @TimeToLive
    @Value("${spring.data.redis.cache.ttl.user}")
    private int ttl;
    @Version
    private long version;
}