package faang.school.postservice.repository.feed;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FeedRedisRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private ZSetOperations<String, Object> zSetOperations;

    @PostConstruct
    public void init() {
        this.zSetOperations = redisTemplate.opsForZSet();
    }

    public void addPostToFeed(Long userId, Long postId, long timestamp) {
        String feedKey = getFeedKey(userId);
        checkZSetSize(feedKey);
        zSetOperations.add(feedKey, postId, timestamp);
    }

    public Set<Object> getFirst20Posts(Long userId) {
        String feedKey = getFeedKey(userId);
        Set<Object> posts = zSetOperations.range(feedKey, 0, 19);
        if (!posts.isEmpty()) {
            List<Object> postList = new ArrayList<>(posts);
            zSetOperations.remove(feedKey, postList.toArray());
        }
        return posts;
    }

    private String getFeedKey(Long userId) {
        return "feed:" + userId;
    }

    private void checkZSetSize(String feedKey) {
        Long size = zSetOperations.size(feedKey);
        if (size >= 500) {
            zSetOperations.removeRange(feedKey, 0, 0);
        }
    }

    public void deleteBatchPost(List<Long> postIds, Long userId) {

    }
}
