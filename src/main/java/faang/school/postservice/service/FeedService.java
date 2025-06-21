package faang.school.postservice.service;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostFeedResponseDto;
import faang.school.postservice.entity.CachedPost;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final UserContext context;
    private final StringRedisTemplate stringRedisTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostMapper mapper;
    private final PostCacheService cacheService;

    @Value("${spring.data.redis.feed.limit}")
    private long limit;

    @Value("${spring.data.redis.feed.size}")
    private long feedSize;

    private static final String USER_FEED_KEY = "user:feed:%s";
    private static final String POSTS_HASH_KEY = "posts:";

    public List<PostFeedResponseDto> getFeed(Long afterPostId) {
        long userId = context.getUserId();
        String userKey = String.format(USER_FEED_KEY, userId);
        Set<String> postIds;

        if (afterPostId == null) {
            postIds = stringRedisTemplate.opsForZSet().range(userKey, 0, limit - 1);
        } else {
            Long rank = stringRedisTemplate.opsForZSet().rank(userKey, afterPostId);
            if (rank != null) {
                postIds = stringRedisTemplate.opsForZSet().range(userKey, (rank + 1), (rank + limit));
            } else {
                fillFeedFromDataBase();
                postIds = stringRedisTemplate.opsForZSet().range(userKey, 0, limit - 1);
            }
        }

        if (postIds == null || postIds.isEmpty()) {
            log.debug("Для юзера {} не нашлось постов в кеше, иду в базу", userId);
            fillFeedFromDataBase();
            postIds = stringRedisTemplate.opsForZSet().range(userKey, 0, limit - 1);
        }

        if (postIds == null) {
            postIds = Set.of();
        }
        return postIds.stream()
                .map(this::getOrLoadPost)
                .filter(Objects::nonNull)
                .map(mapper::toPostFeedResponseDto)
                .toList();
    }


    private CachedPost getOrLoadPost(String postIdStr) {
        Object cached = redisTemplate.opsForHash().get(POSTS_HASH_KEY, postIdStr);
        if (cached instanceof CachedPost post) {
            return post;
        }

        try {
            Long postId = Long.parseLong(postIdStr);
            CachedPost fromBd = getCachedPostFromDb(postId);
            cacheService.cachePost(fromBd);
            log.debug("Пост {} загружен из БД и закеширован", postIdStr);
            return fromBd;
        } catch (EntityNotFoundException e) {
            log.debug("Пост {} не найден в БД", postIdStr);
            return null;
        }
    }


    private CachedPost getCachedPostFromDb(Long postId) {
        return postRepository.findById(postId)
                .map(mapper::toCachedPost)
                .orElseThrow(() -> new EntityNotFoundException("Пост с id %d не найдет", postId));
    }

    private void fillFeedForUser(Long userId, List<Long> followees) {
        List<CachedPost> postToAdd;

        if (followees.isEmpty()) {
            log.debug("У пользователя {} нет подписок", userId);
            postToAdd = cacheService.getGlobalFeed(feedSize);
        } else {
            List<Post> posts = postRepository.findTopPostsByAuthors(followees, feedSize);
            postToAdd = posts.stream()
                    .map(mapper::toCachedPost)
                    .toList();

            if (postToAdd.size() < feedSize) {
                long remaining = (feedSize - postToAdd.size());
                List<CachedPost> globalPosts = cacheService.getGlobalFeed(remaining);

                Set<Long> existPostIds = postToAdd.stream()
                        .map(CachedPost::getId)
                        .collect(Collectors.toSet());

                List<CachedPost> additionalPosts = globalPosts.stream()
                        .filter(p -> !existPostIds.contains(p.getId()))
                        .limit(remaining)
                        .toList();

                postToAdd = Stream.concat(postToAdd.stream(), additionalPosts.stream())
                        .sorted(Comparator.comparing(CachedPost::getPublishedAt).reversed())
                        .toList();
            }
        }

        for (CachedPost post : postToAdd) {
            cacheService.cachePost(post);
            cacheService.addToUserFeed(post, userId);
        }
    }

    private void fillFeedFromDataBase() {
        Long userId = context.getUserId();
        List<Long> followees = userRepository.findFolloweesIdsByUserId(userId);
        fillFeedForUser(userId, followees);
    }

    public void heatFeedForUser(Long userId) {
        List<Long> followees = userRepository.findFolloweesIdsByUserId(userId);
        fillFeedForUser(userId, followees);
    }


}
