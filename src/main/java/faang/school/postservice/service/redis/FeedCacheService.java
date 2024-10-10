package faang.school.postservice.service.redis;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.FeedDto;
import faang.school.postservice.dto.FeedPostDto;
import faang.school.postservice.dto.Post.PostInfoDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.dto.user.UserInfoDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedCacheService {
    @Value(value = "${news-feed.feed.posts_size}")
    private int maxFeedAmount;
    @Value(value = "${news-feed.feed.amount}")
    private int amount;
    @Value(value = "${news-feed.cache.suffix.feed}")
    private String feedSuffix;
    @Value(value = "${news-feed.cache.suffix.post}")
    private String postSuffix;
    @Value(value = "${news-feed.cache.suffix.user}")
    private String userSuffix;
    @Value("${news-feed.feed.max_retries}")
    private int maxAttempts;
    @Value("${news-feed.feed.max_comments}")
    private int commentsAmount;

    private final UserServiceClient userServiceClient;
    private final CommentService commentService;
    private final RedisTemplate<String, Long> feedRedisTemplate;
    private final PostRepository postRepository;
    private final RedissonClient redissonClient;
    private final RedisTransactionsService redisTransactionsService;

    public TreeSet<FeedDto> getFeedDtoResponse(Set<Long> postIds) {
        TreeSet<FeedDto> feed = new TreeSet<>(Comparator.comparing(f -> f.getPostInfo().getUpdatedAt()));
        postIds.forEach(id -> feed.add(new FeedDto(getUserInfo(id), getPostInfo(id))));
        return feed;
    }

    public UserInfoDto getUserInfo(long userId) {
        Supplier<UserInfoDto> dataSupplier = () -> {
            UserDto user = userServiceClient.getUser(userId);
            return new UserInfoDto(user.getId(), user.getUsername());
        };
        return getDtoInfo(userId, dataSupplier, userSuffix);
    }

    public PostInfoDto getPostInfo(Long postId) {
        Supplier<PostInfoDto> databaseConsumer = () -> {
            Optional<Post> post = postRepository.findById(postId);
            if (post.isPresent()) {
                PostInfoDto postInfoDto = new PostInfoDto();
                postInfoDto.setPostContent(post.get().getContent());
                postInfoDto.setUpdatedAt(post.get().getUpdatedAt());
                postInfoDto.setLikes(post.get().getLikes().size());
                UserDto postAuthor = userServiceClient.getUser(post.get().getAuthorId());
                postInfoDto.setDto(new UserInfoDto(postAuthor.getId(), postAuthor.getUsername()));
                postInfoDto.setComments(getLastComments(postId));
                return postInfoDto;
            } else {
                return null;
            }
        };
        return getDtoInfo(postId, databaseConsumer, postSuffix);
    }

    public LinkedHashSet<LastCommentDto> getLastComments(Long postId) {
        LinkedHashSet<LastCommentDto> listDto = new LinkedHashSet<>(commentsAmount);
        List<CommentDto> lastComments = commentService.getAllCommentsByPostIdSortedByCreatedDate(postId);
        lastComments.stream().map(comment -> {
            UserDto author = userServiceClient.getUser(comment.getAuthorId());
            LastCommentDto commentDto = new LastCommentDto();
            commentDto.setComment(comment.getContent());
            commentDto.setAuthor(author.getUsername());
            commentDto.setCreatedAt(comment.getUpdatedAt());
            return commentDto;
        }).limit(commentsAmount).forEach(listDto::add);
        return listDto;
    }

    public <T> T getDtoInfo(Long id, Supplier<T> databaseSearch, String suffix) {
        int attempts = 0;
        String userKey = suffix + ":" + id;
        RMapCache<Long, T> cache = redissonClient.getMapCache(suffix);
        RLock lock = redissonClient.getLock(userKey);
        while (attempts < maxAttempts) {
            attempts++;
            try {
                if (lock.tryLock(1, 5, TimeUnit.SECONDS)) {
                    try {
                        T infoDto = cache.get(id);
                        if (infoDto != null) {
                            lock.unlock();
                            return infoDto;
                        }
                        infoDto = databaseSearch.get();
                        if (infoDto != null) {
                            cache.put(id, infoDto);
                            lock.unlock();
                            return infoDto;
                        } else {
                            return null;
                        }
                    } finally {
                        lock.unlock();
                    }
                }

            } catch (Exception e) {
                log.error("error within getting info because it is locked", e);
            }
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        log.error("haven't managed to get info after {} attempts", maxAttempts);
        return null;
    }

    public Set<Long> getPostsIdsForUser(Long userId, FeedPostDto dto) {
        if (dto == null) {
            return getPostsForUser(userId, amount);
        } else {
            if (dto.containsPageAmount() && !dto.containsPostId()) {
                return getPostsForUser(userId, dto.getPageAmount());
            } else if (dto.containsPostId() && dto.containsPageAmount()) {
                return getPostsForUserFromPostId(userId, dto.getPostId(), dto.getPageAmount());
            } else {
                return getPostsForUserFromPostId(userId, dto.getPostId(), amount);
            }
        }
    }


    private Set<Long> getPostsForUser(Long userId, int amount) {

        Set<Long> postsIds = feedRedisTemplate.opsForZSet().range(feedSuffix + ":" + userId, 0, amount);
        if (postsIds != null && (postsIds.isEmpty() || postsIds.size() < amount)) {
            postsIds.addAll(getPostsIdsFromDBForUser(userId, postsIds.size(), amount - postsIds.size()));
        }
        return postsIds;
    }

    private Set<Long> getPostsForUserFromPostId(Long userId, Long postId, int amount) {
        Set<Long> postsIds = feedRedisTemplate.opsForZSet().range(feedSuffix + ":" + userId, postId - 1, postId + amount - 1);
        if (postsIds != null && (postsIds.isEmpty() || postsIds.size() < amount)) {
            postsIds.addAll(getPostsIdsFromDBForUser(userId, (int) (postId + postsIds.size()), amount - postsIds.size()));
        }
        return postsIds;
    }

    private List<Long> getPostsIdsFromDBForUser(Long userId, int postId, int amount) {
        return postRepository.findPosts(userId, postId, amount);
    }

    @Async
    public void addPost(Long userId, Long postId) {
        String userKey = postSuffix + ":" + userId;
        BiConsumer<RedisOperations<String, Long>, String> addPost = (operations, key) -> {
            operations.opsForList().leftPush(key, postId);
            operations.opsForList().trim(key, 0, maxFeedAmount - 1);
        };
        redisTransactionsService.implementOperation(userKey, postId, maxAttempts, addPost);
        log.info("post {} was added to feed", postId);
    }
}
