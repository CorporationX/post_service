package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedCache;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.redis.cache.RedisUserCache;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@Setter
@RequiredArgsConstructor
public class FeedService {
    private final RedisFeedCache feedCache;
    private final RedisUserCache userCache;
    private final RedisPostCache postCache;
    private final PostService postService;
    private final UserServiceClient userServiceClient;

    @Value("${post.feed.get-batch-size}")
    private int feedBatchSize;

    public List<PostForFeedDto> getFeed(Long userId) {
        Optional<Feed> userFeed = feedCache.findById(userId);
        Optional<List<PostDto>> postsFromCache = userFeed.map(mapFeedToPostDtos());
        List<PostDto> fullPostsBatch = getFullPostsBatch(userId, postsFromCache);

        return collectUserFeed(fullPostsBatch);
    }

    public List<PostForFeedDto> getFeed(Long userId, Long lastViewedPostId) {
        Optional<Feed> userFeed = feedCache.findById(userId);
        Optional<List<PostDto>> postsFromCache = userFeed.map(mapFeedToPostDtos(lastViewedPostId));
        List<PostDto> fullPostsBatch = getFullPostsBatch(userId, postsFromCache);

        return collectUserFeed(fullPostsBatch);
    }

    private List<PostDto> getFullPostsBatch(Long userId, Optional<List<PostDto>> postsFromCache) {
        List<PostDto> fullPostsBatch = new ArrayList<>(
                postsFromCache.orElseGet(() -> postService.getPostsBatchByUserId(userId, feedBatchSize, Optional.empty()))
        );

        int feedLack = feedBatchSize - fullPostsBatch.size();
        if (feedLack > 0) {
            PostDto postPointer = fullPostsBatch.get(fullPostsBatch.size() - 1);
            fullPostsBatch.addAll(postService.getPostsBatchByUserId(userId, feedLack, Optional.of(postPointer)));
        }
        return fullPostsBatch;
    }

    private Function<Feed, List<PostDto>> mapFeedToPostDtos() {
        return feed -> feed.getPostsIds().stream()
                .sorted(Comparator.reverseOrder())
                .limit(feedBatchSize)
                .map(this::getPostDto)
                .toList();
    }

    /**
     * @param lastViewedPostId id of post-pointer
     * @return posts before passed post
     */
    private Function<Feed, List<PostDto>> mapFeedToPostDtos(Long lastViewedPostId) {
        return feed -> {
            List<Long> cachedIds = feed.getPostsIds().stream()
                    .sorted(Comparator.reverseOrder())
                    .toList();

            int lastViewedPostIndex = cachedIds.indexOf(lastViewedPostId);

            return cachedIds.stream()
                    .skip(lastViewedPostIndex)
                    .limit(feedBatchSize)
                    .map(this::getPostDto)
                    .toList();
        };
    }

    private List<PostForFeedDto> collectUserFeed(List<PostDto> fullPostsBatch) {
        return fullPostsBatch.stream()
                .map(postDto -> PostForFeedDto.builder()
                        .post(postDto)
                        .postAuthor(getPostAuthorDto(postDto.getAuthorId()))
                        .build())
                .toList();
    }

    private UserDto getPostAuthorDto(Long authorId) {
        return userCache.findById(authorId)
                .orElseGet(() -> userServiceClient.getUser(authorId));
    }

    private PostDto getPostDto(Long postId) {
        return postCache.findById(postId)
                .orElseGet(() -> postService.getPostById(postId));
    }
}
