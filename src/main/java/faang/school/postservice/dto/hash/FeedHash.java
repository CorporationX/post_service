package faang.school.postservice.dto.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@RedisHash("feed")
public class FeedHash implements Serializable {
    @Id
    private Long id;

    private Set<Long> postIds = new LinkedHashSet<>();

    @Version
    private Long version;
}
