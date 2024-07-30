package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentForFeedDto;
import faang.school.postservice.dto.event.FeedHeatEventDto;
import faang.school.postservice.dto.event.PostViewEventDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.post.PostForFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.kafka.producer.KafkaFeedHeatEventProducer;
import faang.school.postservice.kafka.producer.KafkaPostViewEventProducer;
import faang.school.postservice.redis.cache.Feed;
import faang.school.postservice.redis.cache.RedisFeedCache;
import faang.school.postservice.redis.cache.RedisPostCache;
import faang.school.postservice.redis.cache.RedisUserCache;
import faang.school.postservice.service.ListSplitter;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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
    private final KafkaPostViewEventProducer postViewEventProducer;
    private final KafkaFeedHeatEventProducer feedHeatEventProducer;

    @Value("${post.feed.get-batch-size}")
    private int feedBatchSize;

    @Value("${spring.data.redis.feed-cache.heater-batch-size}")
    public int heaterBatchSize;


    /**
     * Returns feed for specified user
     *
     * @param userId           whose feed needs to be returned
     * @param lastViewedPostId optional parameter specifying last viewed post.
     *                         All returned feed will have date of publication before this post.
     * @return list of special dtos describing posts in users feed (Post author, views counter, likes counter, and first 3 comments)
     */
    public List<PostForFeedDto> getFeed(Long userId, Long lastViewedPostId) {
        Optional<Feed> userFeed = feedCache.findById(userId);
        Optional<List<PostForFeedDto>> postsFromCache;

        if (lastViewedPostId == null) {
            postsFromCache = userFeed.map(mapFeedToPostDtos());
        } else {
            postsFromCache = userFeed.map(mapFeedToPostDtos(lastViewedPostId));
        }

        List<PostForFeedDto> fullPostsBatch = getFullPostsBatch(userId, postsFromCache);
        collectUserFeed(fullPostsBatch);

        handlePostViews(fullPostsBatch);

        return fullPostsBatch;
    }

    private void handlePostViews(List<PostForFeedDto> readyToViewFeed) {
        readyToViewFeed.stream()
                .map(post -> new PostViewEventDto(post.getPostId()))
                .forEach(postViewEventProducer::handleNewPostView);
    }

    private List<PostForFeedDto> getFullPostsBatch(Long userId, Optional<List<PostForFeedDto>> postsFromCache) {
        List<PostForFeedDto> fullPostsBatch = new ArrayList<>(
                postsFromCache.orElseGet(() -> postService.getFeedForUser(userId, feedBatchSize, Optional.empty()))
        );

        int feedLack = feedBatchSize - fullPostsBatch.size();
        if (feedLack > 0) {
            Optional<Long> postPointerId = Optional.empty();
            if (fullPostsBatch.size() > 0) {
                postPointerId = Optional.of(fullPostsBatch.get(fullPostsBatch.size() - 1).getPostId());
            }

            fullPostsBatch.addAll(postService.getFeedForUser(userId, feedLack, postPointerId));
        }
        return fullPostsBatch;
    }

    private Function<Feed, List<PostForFeedDto>> mapFeedToPostDtos() {
        return feed -> {
            if (feed.getPostsIds() == null) {
                return null;
            }

            return feed.getPostsIds().stream()
                    .sorted(Comparator.reverseOrder())
                    .limit(feedBatchSize)
                    .map(this::getPostDto)
                    .toList();
        };
    }

    /**
     * @param lastViewedPostId id of post-pointer
     * @return posts published before passed post-pointer
     */
    private Function<Feed, List<PostForFeedDto>> mapFeedToPostDtos(Long lastViewedPostId) {
        return feed -> {
            List<Long> cachedIds = feed.getPostsIds().stream()
                    .sorted(Comparator.reverseOrder())
                    .toList();

            int lastViewedPostIndex = cachedIds.indexOf(lastViewedPostId);

            return cachedIds.stream()
                    .skip(lastViewedPostIndex + 1)
                    .limit(feedBatchSize)
                    .map(this::getPostDto)
                    .toList();
        };
    }

    private void collectUserFeed(List<PostForFeedDto> fullPostsBatch) {
        fullPostsBatch.forEach(postForFeedDto -> {
            postForFeedDto.setPostAuthor(getUserDto(postForFeedDto.getPostAuthorId()));

            Set<CommentForFeedDto> comments = postForFeedDto.getComments();
            if (comments != null) {
                comments.forEach(setCommentAuthor());
            }

            postForFeedDto.incrementVersion();
        });
    }

    private Consumer<CommentForFeedDto> setCommentAuthor() {
        return commentForFeed -> {
            Long authorId = commentForFeed.getCommentDto().getAuthorId();
            commentForFeed.setCommentAuthor(getUserDto(authorId));
        };
    }

    private UserDto getUserDto(Long userId) {
        return userCache.findById(userId)
                .orElseGet(() -> userServiceClient.getUser(userId));
    }

    private PostForFeedDto getPostDto(Long postId) {
        return postCache.findById(postId)
                .orElseGet(
                        () -> {
                            PostDto post = postService.getPostById(postId);
                            return PostForFeedDto.builder()
                                    .postId(postId)
                                    .postAuthorId(post.getAuthorId())
                                    .content(post.getContent())
                                    .publishedAt(post.getPublishedAt())
                                    .likesList(new ArrayList<>())
                                    .viewsCounter(0L)
                                    .comments(new LinkedHashSet<>())
                                    .build();
                        }
                );
    }

    public void heatFeed() {
        List<Long> userIds = userServiceClient.getAllUsersIds();
        List<List<Long>> batchedUserIds = ListSplitter.splitList(userIds, heaterBatchSize);

        batchedUserIds.stream()
                .map(FeedHeatEventDto::new)
                .forEach(feedHeatEventProducer::handleFeedHeating);
    }
}
