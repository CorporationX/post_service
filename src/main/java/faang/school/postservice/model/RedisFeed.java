package faang.school.postservice.model;

import org.springframework.data.annotation.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.LinkedHashSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Feed")
public class RedisFeed {

    @Id
    private Long userId;

    private LinkedHashSet<PostPair> posts;

    @Version
    private Long version;
}
