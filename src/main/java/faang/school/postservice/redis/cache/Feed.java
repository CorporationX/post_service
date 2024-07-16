package faang.school.postservice.redis.cache;

import faang.school.postservice.dto.event.PostEventDto;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.concurrent.TimeUnit;

@Data
@RedisHash("Feed")
public class Feed implements Serializable {
    @Id
    private Long userId;

    /**
     * Top n feed posts
     */
    private LinkedHashSet<Long> postsIds;

    @TimeToLive(unit = TimeUnit.DAYS)
    private int ttl;

    @Version
    private long version;


    public Feed(Long userId, int ttl) {
        this.userId = userId;
        postsIds = new LinkedHashSet<>();
        this.ttl = ttl;
    }

    public void addNewPost(PostEventDto postEventDto, int maxPostsAmount) {
        if (postsIds.size() >= maxPostsAmount) {
            Long firstElement = postsIds.iterator().next();
            postsIds.remove(firstElement);
        }

        postsIds.add(postEventDto.getPostId());
        version++;

    }
}
