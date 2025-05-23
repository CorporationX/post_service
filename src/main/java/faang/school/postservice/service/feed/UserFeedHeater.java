package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.properties.FeedCacheProperties;
import faang.school.postservice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserFeedHeater {

    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final FeedService feedService;
    private final FeedCacheProperties feedCacheProperties;


    @Async("feedHeaterExecutor")
    public void heatUser(UserDto user) {
        try {
            List<Long> followeeIds = userServiceClient.getFollowerIds(user.id());
            if (followeeIds.isEmpty()) return;

            List<Post> posts = postRepository.findLatestPostsByAuthors(followeeIds, feedCacheProperties.getMaxFeedSize())
                    .stream()
                    .limit(500)
                    .toList();

            for (Post post : posts) {
                feedService.cachePost(post);
                feedService.cacheAuthor(post.getAuthorId());
            }

            List<Long> postIds = posts.stream()
                    .map(Post::getId)
                    .toList();

            feedService.rebuildFeed(user.id(), postIds);

        } catch (Exception e) {
            log.error("❌ Ошибка прогрева фида user {}", user.id(), e);
        }
    }
}
