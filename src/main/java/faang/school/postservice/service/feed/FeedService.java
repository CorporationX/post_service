package faang.school.postservice.service.feed;

import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.mapper.feed.FeedMapper;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final FeedMapper feedMapper;

    @Value("${spring.data.redis.cache.capacity.max.news_feed}")
    private int limitNewsFeed;

    public List<PostFeedDto> getNewsFeed(Long postId, long userId) {

        if (postId == null) {
            return getLimitNewsFeed(userId);
        } else {
            return List.of(getOneNews(postId));
        }
    }

    private List<PostFeedDto> getLimitNewsFeed(long userId) {
        TreeSet<Long> postsId = redisFeedRepository.getPostsIdsByFollowerId(userId, limitNewsFeed).orElse(null);
        log.info("Post IDs: {} by user ID: {}", postsId, userId);

        List<PostRedis> postRedis = StreamSupport.stream(redisPostRepository.findAllById(postsId).spliterator(), false).toList();
        log.info("List PostRedis in Redis: {}", postRedis);

        List<Long> userIds = postRedis.stream().map(PostRedis::getAuthorId).toList();
        Map<Long, UserRedis> userRedisMap = StreamSupport.stream(redisUserRepository.findAllById(userIds).spliterator(), false)
                .collect(Collectors.toMap(UserRedis::getId, user -> user));
        log.info("All authors posts: {}", userRedisMap.values());

        return postRedis.stream().map(i -> {
            PostFeedDto postFeedDto = feedMapper.toPostFeedDto(i);
            String userName = userRedisMap.get(postFeedDto.getAuthorId()).getUsername();
            postFeedDto.setAuthorName(userName);
            return postFeedDto;
        }).toList();
    }

    private PostFeedDto getOneNews(long postId) {
        return redisPostRepository.findById(postId).flatMap(postRedis ->
                redisUserRepository.findById(postRedis.getAuthorId()).map(userRedis -> {
                    PostFeedDto postFeedDto = feedMapper.toPostFeedDto(postRedis);
                    postFeedDto.setAuthorName(userRedis.getUsername());
                    return postFeedDto;
                })
        ).orElseGet(() -> {
            log.info("Post by ID: {} or user by author ID not found", postId);
            return null;
        });
    }
}
