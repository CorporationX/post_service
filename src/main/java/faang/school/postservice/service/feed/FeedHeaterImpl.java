package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.kafka.KafkaProperties;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.AuthorCacheRepository;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.repository.PostCacheRepository;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.FeedHeater;
import faang.school.postservice.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public class FeedHeaterImpl implements FeedHeater {
    private final ThreadPoolTaskExecutor feedExecutor;
    private final UserServiceClient userClient;
    private final KafkaProperties kafkaProperties;
    private final AuthorCacheRepository authorCacheRepository;
    private final PostRepository postRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostMapper postMapper;
    private final PostCacheService postCacheService;
    private final FeedCacheRepository feedCacheRepository;

    @Override
    public void heatAll() {
        heatUsersCache();
        heatPostsCache();
        heatFeed();
    }

    private void heatUsersCache() {
        long userCount = userClient.getUserCount();
        for (long i = 0; i < userCount; i += kafkaProperties.maxBatchSize()) {
            long offset = i;
            CompletableFuture.runAsync(() -> {
                List<UserDto> users = userClient.getUsersPage(offset, kafkaProperties.maxBatchSize());
                users.forEach(user -> feedExecutor.execute(() -> authorCacheRepository.set(user)));
            }, feedExecutor);
        }
    }

    private void heatPostsCache() {
        long postsCount = postRepository.count();
        for (long i = 0; i < postsCount; i += kafkaProperties.maxBatchSize()) {
            long offset = i;
            CompletableFuture.runAsync(() -> {
                List<Post> posts = postRepository.findPage(offset, kafkaProperties.maxBatchSize());
                posts.forEach(post -> feedExecutor.execute(() -> postCacheRepository.set(postMapper.toCacheDto(post))));
                posts.forEach(post -> postCacheService.addView(post.getId()));
            }, feedExecutor);
        }
    }

    private void heatFeed() {
        long userCount = userClient.getUserCount();
        for (long i = 0; i < userCount; i += kafkaProperties.maxBatchSize()) {
            long offset = i;
            CompletableFuture.runAsync(() -> {
                List<Post> posts = postRepository.findPage(offset, kafkaProperties.maxBatchSize());
                HashMap<Long, List<Long>> postFollowers = new HashMap<>();
                posts.forEach((post) -> {
                    postFollowers.put(post.getId(), userClient.getFollowers(post.getAuthorId()).stream()
                            .map(UserDto::id)
                            .toList());
                });

                postFollowers.forEach(this::setPostToUsers);
            }, feedExecutor);
        }
    }

    private void setPostToUsers(long postId, List<Long> batch) {
        batch.forEach(userId -> feedCacheRepository.set(postId, userId));
    }
}
