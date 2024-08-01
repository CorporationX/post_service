package faang.school.postservice.dto.feed;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@RedisHash("Feed")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Feed implements Serializable {

    @Id
    private Long userId;

    private Set<Long> postIds;

    @Version
    private Long version;

    public Feed(Long userId) {
        this.userId = userId;
        postIds = new LinkedHashSet<>();
        version = 0L;
    }

    public void addNewPost(long postId, int maxSize) {
        if (postIds.size() >= maxSize) {
            long firstPost = postIds.iterator().next();
            postIds.remove(firstPost);
        }
        postIds.add(postId);
        version++;
    }
}