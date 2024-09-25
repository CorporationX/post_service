package faang.school.postservice.heater;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.comment.CommentCache;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.CacheCommentAuthorMapper;
import faang.school.postservice.mapper.CacheCommentMapper;
import faang.school.postservice.mapper.CachePostMapper;
import faang.school.postservice.model.CacheCommentAuthor;
import faang.school.postservice.model.CacheUser;
import faang.school.postservice.model.post.CachePost;
import faang.school.postservice.repository.RedisAuthorCommentRepository;
import faang.school.postservice.repository.RedisPostRepository;
import faang.school.postservice.repository.RedisUserRepository;
import faang.school.postservice.service.CommentService;
import faang.school.postservice.service.PostService;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FeedHeater {
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;
    private final PostService postService;
    private final RedisPostRepository redisPostRepository;
    private final RedisAuthorCommentRepository redisAuthorCommentRepository;
    private final CommentService commentService;
    private final CachePostMapper cachePostMapper;
    private final CacheCommentMapper cacheCommentMapper;
    private final CacheCommentAuthorMapper cacheCommentAuthorMapper;

    @Value("${spring.user.ttl}")
    private int userTtl;
    @Value("${batch-size}")
    private int batchSize;

    public void heatFeed() {
        List<UserDto> usersDtos = userServiceClient.getAllUsers();
        List<List<UserDto>> batches = splitList(usersDtos, batchSize);

        batches.forEach(this::fillFeed);
    }

    @Async("executorService")
    public void fillFeed(List<UserDto> usersDtos) {
        List<CacheUser> cacheUsers = convertUserDtosToCacheUsers(usersDtos);
        Set<CacheCommentAuthor> cacheCommentAuthors = new HashSet<>();
        HashSet<CachePost> cachePosts = new HashSet<>();

        cacheUsers.forEach(cacheUser -> {
            List<CachePost> posts = getCachePosts(cacheUser.getFeed().stream().toList());
            cachePosts.addAll(posts);

            posts.forEach(post -> {
                List<CommentCache> commentCaches = cacheCommentMapper
                        .ConvertCommentDtosToCacheComments(commentService.findAllByPostId(post.getId()));
                post.setComments(new LinkedHashSet<>(commentCaches));


                List<Long> authorIds = commentCaches.stream().map(CommentCache::getAuthorId).toList();
                cacheCommentAuthors.addAll(getCacheCommentAuthors(authorIds));
            });
        });
        saveCache(cacheUsers, cacheCommentAuthors, cachePosts);
    }

    public void saveCache(List<CacheUser> cacheUsers,
                          Set<CacheCommentAuthor> cacheCommentAuthors,
                          HashSet<CachePost> cachePosts) {
        redisUserRepository.saveAll(cacheUsers);
        redisPostRepository.saveAll(cachePosts);
        redisAuthorCommentRepository.saveAll(cacheCommentAuthors);
    }

    private List<CacheUser> convertUserDtosToCacheUsers(List<UserDto> usersDtos) {
        return usersDtos.stream().map(userDto -> {
            List<CachePost> userFeed = getPostToFeed(userDto.getId());
            return CacheUser.builder()
                    .id(userDto.getId())
                    .feed(new LinkedHashSet<>(userFeed.stream().map(CachePost::getId).toList()))
                    .ttl(userTtl)
                    .build();
        }).toList();
    }

    private List<CachePost> getPostToFeed(long userId) {
        List<CachePost> userFeed = postService.getPostsByAuthorsIds(
                userServiceClient.getFollowingIds(userId));
        redisPostRepository.saveAll(userFeed);
        return userFeed;
    }

    private List<CachePost> getCachePosts(List<Long> postIds) {
        return cachePostMapper.convertPostsToCachePosts(postService.getPostsByIdsWithLikes(postIds));
    }

    private List<CacheCommentAuthor> getCacheCommentAuthors(List<Long> authorsIds) {
        return cacheCommentAuthorMapper.toCacheCommentAuthor(userServiceClient.getUsersByIds(authorsIds));
    }

    private <T> List<List<T>> splitList(List<T> list, int size) {
        List<List<T>> batches = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            return batches;
        }
        for (int i = 0; i < list.size(); i += size) {
            batches.add(new ArrayList<>(list.subList(i, Math.min(i + size, list.size()))));
        }
        return batches;
    }
}
