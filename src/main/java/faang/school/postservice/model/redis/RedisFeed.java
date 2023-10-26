package faang.school.postservice.model.redis;

import faang.school.postservice.dto.redis.TimePostId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "Feed")
public class RedisFeed implements Serializable {

    @Id
    private long userId;
    private SortedSet<TimePostId> postIds;
    @Version
    private Long version;
}
