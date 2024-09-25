package faang.school.postservice.service.redis;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.RequestFeedDto;
import faang.school.postservice.dto.post.PostFeedDto;
import faang.school.postservice.model.redis.FeedForCache;
import faang.school.postservice.model.redis.PostForCache;
import faang.school.postservice.repository.redis.FeedRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final FeedRepository feedRepository;
    private final PostCacheService postCacheService;
    private final PostForFeedService postForFeedService;
    private final UserContext userContext;

    @Value("${cache.batch_size}")
    private int batchSize;

    public List<PostFeedDto> getPostFeedDtos(RequestFeedDto requestFeedDto) {
        Long userId = userContext.getUserId();
        FeedForCache feed = feedRepository.findByUserId(userId);
        List<Long> postIdBatch = new ArrayList<>();
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
                    PostFeedDto postDtoForFeedFromCache = postForFeedService.getPostDtoForFeed(postForCache);
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
}