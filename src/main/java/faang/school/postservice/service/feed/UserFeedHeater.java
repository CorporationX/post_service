package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.feed.FeedCacheProperties;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFeedHeater {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final FeedCacheService feedCacheService;
    private final FeedManagementService feedManagementService;
    private final FeedCacheProperties feedCacheProperties;


    @Async("feedHeaterExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void heatUser(UserDto user) {
        try {
            List<Long> followeeIds = userServiceClient.getFollowerIds(user.id());
            if (followeeIds.isEmpty()) return;

            List<Post> posts = postRepository.findLatestPostsByAuthors(
                    followeeIds,
                    feedCacheProperties.getMaxFeedSize()
            );


            for (Post post : posts) {
                feedCacheService.cachePostAndAuthor(post);
            }

            List<Long> postIds = posts.stream()
                    .map(Post::getId)
                    .toList();

            feedManagementService.rebuildFeed(user.id(), postIds);

        } catch (Exception e) {
            log.error("User feed warmup error {}", user.id(), e);
        }
    }
}
