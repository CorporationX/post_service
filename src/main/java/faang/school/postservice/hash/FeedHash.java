package faang.school.postservice.hash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("FeedHash")
public class FeedHash implements Serializable {

    @Id
    private Long userId;

    private LinkedHashSet<Long> postIds;

    @Version
    private long version;
}
