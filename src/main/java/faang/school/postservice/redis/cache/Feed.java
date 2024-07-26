package faang.school.postservice.redis.cache;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "Feed")
public class Feed implements Serializable {
    @Id
    private Long userId;

    /**
     * Top n feed posts
     */
    private Set<Long> postsIds;

    @Version
    private long version;


    public Feed(Long userId) {
        this.userId = userId;
        postsIds = new LinkedHashSet<>();
        version = 0;
    }

    public Feed(Long userId, LinkedHashSet<Long> postsIds) {
        this.userId = userId;
        this.postsIds = postsIds;
        version++;
    }

    public void addNewPost(long postId, int maxPostsAmount) {
        if (postsIds.size() >= maxPostsAmount) {
            Long firstElement = postsIds.iterator().next();
            postsIds.remove(firstElement);
        }

        postsIds.add(postId);
        version++;
    }
}
