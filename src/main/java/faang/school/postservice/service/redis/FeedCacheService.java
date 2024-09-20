package faang.school.postservice.service.redis;

import faang.school.postservice.repository.redis.FeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {
    private final FeedRepository feedRepository;
    private final PostCacheService postService;
    private final UserCacheService userService;
    private final RedisTemplate<Long, Long> feedRedisTemplate;


    public Set<Long> getPostsForUser(Long userId, int amount) {
        return feedRedisTemplate.opsForZSet().range(userId, 0, amount);
//        return getPostsForRange(0L,amount,userId);

    }

    public Set<Long> getPostsForUserFromPostId(Long userId, Long postId, int amount) {
        return feedRedisTemplate.opsForZSet().range(userId, postId - 1, postId + amount - 1);
//        return getPostsForRange(postId,amount,userId);
    }
//
//    private List<Long> getPostsForRange(Long startPostId, int amount, Long userId) {
//
//        Feed userFeed = feedRepository.findById(userId).orElse(null);
//        if (userFeed != null) {
//            List<Long> list = new ArrayList<>(userFeed.getPostsIds());
//            return list.subList(startPostId.intValue(), Math.min(amount, list.size()));
//        } else {
//            return new ArrayList<>();
//        }
//}
}
