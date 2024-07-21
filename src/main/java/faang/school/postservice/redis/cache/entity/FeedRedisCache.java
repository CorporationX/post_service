package faang.school.postservice.redis.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.NavigableSet;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("feed")
public class FeedRedisCache implements Serializable {

    @Id
    private long id;

    @Reference
    @ToString.Exclude
    private NavigableSet<PostRedisCache> posts;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeedRedisCache that = (FeedRedisCache) o;
        return getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Long.hashCode(getId());
    }
}
