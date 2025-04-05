package faang.school.postservice.service.feed;

import faang.school.postservice.broker.producer.FeedHeaterEventProducer;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.feed.NewsFeedProperties;
import faang.school.postservice.dto.post.PostResponseDto;
import faang.school.postservice.dto.subscription.SubscriptionUserDto;
import faang.school.postservice.dto.user.UserResponseDto;
import faang.school.postservice.service.PostService;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeaterServiceImpl implements FeedHeaterService {

    private final NewsFeedProperties newsFeedProperties;
    private final UserService userService;
    private final FeedHeaterEventProducer feedHeaterEventProducer;
    private final FeedService feedService;
    private final PostService postService;
    private final AsyncTaskExecutor asyncTaskExecutor;
    private final UserContext userContext;

    @Override
    public void heatFeed() {
        List<UserResponseDto> users = userService.getAllUsers();
        List<Long> userIds = users.stream().map(UserResponseDto::getId).toList();
        List<List<Long>> usersBatches = ListUtils.partition(userIds, newsFeedProperties.batchSize());

        if (CollectionUtils.isNotEmpty(usersBatches)) {
            usersBatches.forEach(batch -> {
                try {
                    if (CollectionUtils.isNotEmpty(batch)) {
                        feedHeaterEventProducer.produceFeedHeaterEventAsync(batch);
                    }
                } catch (Exception e) {
                    log.error("Failed to process batch: {}", batch, e);
                }
            });
        } else {
            log.warn("Batches list is empty or null");
        }
        log.info("Heat feed event processed");
    }

    @Override
    public void heatFeedByUsersList(List<Long> userIds) {
        userContext.setUserId(0L);
        for (long userId : userIds) {
            List<SubscriptionUserDto> followers = userService.getFollowers(userId);
            List<Long> followersIds = followers.stream().map(SubscriptionUserDto::id).toList();

            if (!followersIds.isEmpty()) {
                List<PostResponseDto> posts = postService.getPostsByUser(userId);
                for (PostResponseDto post : posts) {
                    long postId = post.id();
                    CompletableFuture<Void> result = CompletableFuture.runAsync(() ->
                                            feedService.subProcessExistingPost(postId, followersIds),
                                    asyncTaskExecutor)
                            .thenAccept(res -> {
                                log.info("Post id {} processed for feed heater", postId);
                            })
                            .exceptionally(exception -> {
                                log.error("Error processing post {} for feed heater. Error: {}",
                                        postId, exception.getMessage());
                                return null;
                            });
                }
            }
        }
    }
}
