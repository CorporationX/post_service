package faang.school.postservice.service.redis;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.RequestFeedDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.model.cache.FeedForCache;
import faang.school.postservice.model.cache.PostForCache;
import faang.school.postservice.repository.redis.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final PostCacheService postCacheService;
    private final PostForFeedService postForFeedService;
    private final UserContext userContext;
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${cache.batch_size}")
    private int batchSize;

    @Value("${cache.max_size}")
    private int maxFeedSize;

    public List<PostFeedDto> getPostFeedDtos(RequestFeedDto requestFeedDto) {
        Long userId = userContext.getUserId();
        FeedForCache feed = feedRepository.findByUserId(userId);
        List<Long> postIdBatch;
        Long postId = requestFeedDto.getPostId();
        int amount = requestFeedDto.getAmount();
        if (amount == 0) {
            amount = batchSize;
        }
        if (postId == 0) {
            postIdBatch = feed.getPostsIds()
                    .stream()
                    .limit(amount)
                    .toList();
        } else {
            postIdBatch = feed.getPostsIds()
                    .tailSet(postId)
                    .stream()
                    .limit(amount)
                    .toList();
        }
        List<PostFeedDto> postDtosForFeed = new ArrayList<>();
        List<PostForCache> postBatch = postCacheService.getAllPostsByIds(postIdBatch);
        postBatch.forEach(
                postForCache -> {
                    PostFeedDto postDtoForFeedFromCache = postForFeedService.getPostDtoForFeedFromCache(postForCache);
                    postDtosForFeed.add(postDtoForFeedFromCache);
                });

        int postBatchSize = postBatch.size();
        int requiredNumberOfPosts = amount - postBatchSize;
        if (postBatchSize < postIdBatch.size() || requiredNumberOfPosts > 0) {
            List<PostFeedDto> postDtosForFeedFromDB = postForFeedService.getPostDtosForFeedFromDB(postBatch, postIdBatch);
            postDtosForFeed.addAll(postDtosForFeedFromDB);
        }
        return postDtosForFeed;
    }

    public void addPostToFeed(Long userId, Long postId) {
        String key = "userId";
        Long newValue = postId;
        redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.watch(key.getBytes());
            byte[] currentValueBytes = connection.get(key.getBytes());
            Integer currentValue = currentValueBytes != null ? Integer.parseInt(new String(currentValueBytes)) : 0;
            FeedForCache feed = feedRepository.findByUserId(userId);
            TreeSet<Long> feedPostIds = feed.getPostsIds();
            if (feedPostIds.size() >= maxFeedSize) {
                feedPostIds.remove(feedPostIds.first());
            }
            feedPostIds.add(newValue);
            connection.multi();
            connection.set(key.getBytes(), String.valueOf(feedPostIds).getBytes());
            return connection.exec();
        });
    }
}