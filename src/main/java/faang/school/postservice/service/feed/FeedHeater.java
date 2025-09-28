package faang.school.postservice.service.feed;

import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.event.FeedHeatEvent;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.FailedFeedHeatException;
import faang.school.postservice.producer.FeedHeatProducer;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.user.UserService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedHeater {
    private final FeedProps feedProps;
    private final ExecutorService executor;
    private final UserService userService;
    private final PostService postService;
    private final FeedHeatProducer feedHeatProducer;

    public void start() {
        List<UserDto> users = userService.getBatch(feedProps.user().batch());
        log.info("Got {} users for heat feed", users.size());
        users.forEach(user -> heatFeedAsync(user.id()));
    }

    private void heatFeedAsync(long userId) {
        CompletableFuture.runAsync(() -> {
            List<Long> subs = userService.getSubs(userId);
            log.info("Got {} subs for userId = {}", subs.size(), userId);
            List<PostDto> posts = getRecentPost(subs);
            log.info("Got {} recent posts for userId = {}", posts.size(), userId);
            if (!posts.isEmpty()) {
                feedHeatProducer.sendEvent(new FeedHeatEvent(userId, posts));
            }
        }, executor).exceptionally(ex -> {
            log.error(ex.getMessage(), ex);
            throw new FailedFeedHeatException("Failed to heat feed for userId = {}. Cause: ", ex.getMessage());
        });
    }

    private List<PostDto> getRecentPost(List<Long> authorIds) {
        return postService.findRecentBatchByAuthors(authorIds, null, feedProps.maxStored());
    }
}
