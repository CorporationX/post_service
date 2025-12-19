package faang.school.postservice.dto.redis;

import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@RedisHash(value = "Feed")
@Setter
@Getter
@NoArgsConstructor
public class CachedFeedDto {

    @Id
    private Long userId;
    private LinkedHashSet<Long> postIds;

    @Version
    private Long version;

    public CachedFeedDto(Long userId, LinkedHashSet<Long> postIds) {
        this.userId = userId;
        this.postIds = postIds;
    }
}
