package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.RedisCashMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedCache;
import faang.school.postservice.model.redis.PostCache;
import faang.school.postservice.model.redis.UserCache;
import faang.school.postservice.repository.redis.FeedCacheRepository;
import faang.school.postservice.repository.redis.PostCacheRepository;
import faang.school.postservice.repository.redis.UserCacheRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCashService {
    private final PostCacheRepository postCacheRepository;
    private final UserCacheRepository userCacheRepository;
    private final FeedCacheRepository feedCacheRepository;
    private final RedisCashMapper redisCashMapper;
    private final UserServiceClient userServiceClient;

    //Хотела сделать этот метод асинхронный, чтобы метод PostService не ждал сохранения кэша и завершал транзакцию.
    // Но для сохранения UserCache мы идем за юзером в userServiceClient, где перехватывает FeignUserInterceptor,
    // он должен считать x-user-id этого потока, а асинхронно у нового потока этот хэдэр не заполнен и вылетает ошибка.

    //    @Async(value = "executorService")
    public void saveCache(Post post) {
        savePostCache(redisCashMapper.toCash(post));
        try {
            getAndSaveUserCache(post.getAuthorId());
        } catch (FeignException e) {
            log.error("Error receiving the user", e); // подавляем экзепшн, чтобы не останавливать транзакцию
        }
    }

    public void savePostCache(PostCache postCache) {
        postCacheRepository.save(postCache);
    }

    public void saveUserCache(UserCache userCache) {
        userCacheRepository.save(userCache);
    }

    @Retryable(retryFor = FeignException.class, maxAttempts = 4, backoff = @Backoff(1000))
    public UserDto getAndSaveUserCache(long userId) throws FeignException {
        UserDto user = userServiceClient.getUser(userId);
        saveUserCache(redisCashMapper.toCash(user));
        return user;
    }

    public Optional<PostCache> getPostCache(long postId) {
        return postCacheRepository.findById(postId);
    }

    public void deletePostCache(long postId) {
        postCacheRepository.deleteById(postId);
    }

    public Optional<UserCache> getUserCache(long userId) {
        return userCacheRepository.findById(userId);
    }

    public void saveFeedCache(FeedCache feedCache) {
        feedCacheRepository.save(feedCache);
    }

    public Optional<FeedCache> getFeed(Long user) {
        return feedCacheRepository.findById(user);
    }

    public FeedCache getOrCreateFeed(Long user) {
        return feedCacheRepository.findById(user)
                .orElse(FeedCache.builder().userId(user).postIds(new LinkedHashSet<>()).build());
    }

    @Async(value = "executorService")
    public void createFeedCacheAsync(long userId, List<Post> posts) {
        FeedCache feedCache = new FeedCache(userId, new LinkedHashSet<>());
        if (posts.size() > 1) {
            posts.stream()
                    .sorted(Comparator.comparing(Post::getPublishedAt)) // сортировка работает если в списке больше 1 элемента
                    .forEach(post -> feedCache.getPostIds().add(post.getId()));
        } else {
            feedCache.getPostIds().add(posts.get(0).getId());
        }
        saveFeedCache(feedCache);
    }
}