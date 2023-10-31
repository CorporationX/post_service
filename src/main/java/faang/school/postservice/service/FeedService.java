package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.client.UserDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.mapper.redis.RedisUserMapper;
import faang.school.postservice.model.redis.RedisFeed;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.model.redis.RedisUser;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedService {
    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final RedisUserRepository redisUserRepository;
    private final PostService postService;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final RedisUserMapper redisUserMapper;
    private final RedisPostMapper redisPostMapper;

    @Value("{post.feed.feed-size}")
    private int feedSizeOfPosts; //500
    @PersistenceContext
    private EntityManager entityManager;

    public List<FeedDto> getFeed(Long postId) {
        //TODO если приходит пост id ноль, то есть нет точки откуда начать брать посты => берем 20 первых постов
        long userId = userContext.getUserId();
        Optional<RedisFeed> redisFeed = redisFeedRepository.findById(userId); // ищем id постов тех авторов, на которых подписан юзер
        if (redisFeed.isEmpty()) {
            return getPostsFromDb(userId, postId);
        }
        LinkedHashSet<Long> twentyIdsOfPosts = getTwentyIdsOfPosts(postId, redisFeed.get()); // получить только 20 постов
        if (twentyIdsOfPosts.isEmpty()) {
            return getPostsFromDb(userId, postId);
        }
        List<FeedDto> resultFeed = new ArrayList<>();
        twentyIdsOfPosts.stream()
                .map(id -> checkPostInRedis(postId))
                .forEach(redisPost -> {
            RedisUser redisUser = checkUserInRedis(redisPost.getAuthorId());
            resultFeed.add(buildFeedDto(redisPost, redisUser)); // собираем лист из фидДтошек для юзера
        });
        return resultFeed;
    }

    private List<FeedDto> getPostsFromDb(long userId, Long postId) {
        RedisUser user = checkUserInRedis(userId); // get юзера
        List<PostDto> postsDto;
        if (postId == null) { // нет поста от которого брать следующие
            postsDto = postService.getPostsFromBeginningInDb(user.getFolloweeIds(), feedSizeOfPosts);
        } else {// пост есть -> начинаем брать посты начиная до этого поста который есть в кэше(есть 5 -> берем 6-10 то есть более новые)
            postsDto = postService.getPostsAfterPostInDb(user.getFolloweeIds(), feedSizeOfPosts, postId);
        }
        return postsDto.stream()
                .map(redisPostMapper::toRedisPost) // мап постов в "сущность" пост редис
                .map(redisPost -> {
                    RedisUser redisUser = checkUserInRedis(redisPost.getAuthorId());
                    return buildFeedDto(redisPost, redisUser);
                }).toList();
    }

    private LinkedHashSet<Long> getTwentyIdsOfPosts(Long postId, RedisFeed feed) {
        LinkedHashSet<Long> postIds = feed.getPostIds();
        LinkedHashSet<Long> resultPostIds = new LinkedHashSet<>();
        if (postId == null) { // нет поста от которого брать предыдущие
            return postIds.stream()
                    .limit(feedSizeOfPosts)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        }
        boolean found = false;
        for (Long id : postIds) { // берем посты до поста который есть в кэше
            if (found) {
                resultPostIds.add(id);
                if (resultPostIds.size() >= feedSizeOfPosts) {
                    break;
                }
            }
            if (id.equals(postId)) {
                found = true;
            }
        }
        int missingPostCount = feedSizeOfPosts - resultPostIds.size();
        if (missingPostCount > 0) {
            List<Long> missingPostIds = getMissingPostIdsFromDatabase(missingPostCount, postId);
            resultPostIds.addAll(missingPostIds);
        }
        // postIds.removeAll(resultPostIds); мб удалить пост до которого брали посты
        return resultPostIds;
    }

    private RedisUser checkUserInRedis(long userId) { // юзер либо в userFeed, либо в user_service
        return redisUserRepository.findById(userId)
                .orElseGet(() -> {
                    RedisUser userFromContext = redisUserMapper.toRedisUser(userServiceClient.getUser(userId));
                    userFromContext.setVersion(1L);
                    redisUserRepository.save(userFromContext);
                    return userFromContext;
                });
    }

    private RedisPost checkPostInRedis(long postId) { // пост либо в postFeed, либо в post_service
        return redisPostRepository.findById(postId)
                .orElseGet(() -> {
                    RedisPost postFromPostService = redisPostMapper.toRedisPost(postService.getPost(postId));
                    postFromPostService.setVersion(1L);
                    redisPostRepository.save(postFromPostService);
                    return postFromPostService;
                });
    }

    private List<Long> getMissingPostIdsFromDatabase(int missingPostCount, Long postId) {
        return postRepository.getMissingPostIds(missingPostCount, postId, entityManager);
    } //идем за недостоющими айдишниками постов

    private FeedDto buildFeedDto(RedisPost redisPost, RedisUser redisUser) { // вид фида для юзера
        return FeedDto.builder()
                .postId(redisPost.getId())
                .authorName(redisUser.getUsername())
                .content(redisPost.getContent())
                .likes((long) redisPost.getPostLikes())
                .comments(redisPost.getComments())
                .createdAt(redisPost.getPublishedAt())
                .updatedAt(redisPost.getUpdatedAt())
                .build();
    }

     public void heatFeed(UserDto userDto) {
        List<Long> followeeIds = userDto.getFollowees();
        List<PostDto> firstPostsFromBeginning = postService.getPostsFromBeginningInDb(followeeIds, feedSizeOfPosts);
        firstPostsFromBeginning.forEach(postDto -> {
            if (!redisPostRepository.existsById(postDto.getId())) { //чек поста в кэше
                redisPostRepository.save(redisPostMapper.toRedisPost(postDto));
            }
            checkUserInRedis(postDto.getAuthorId()); // пост есть -> проверяем юзера в кэше, иначе сохраняем его
        });
        if (redisFeedRepository.findById(userDto.getId()).isEmpty()) { // если у юзера пустой фид -> делаем и сохраняем фид
            RedisFeed redisFeed = RedisFeed.builder()
                    .userId(userDto.getId())
                    .postIds(firstPostsFromBeginning.stream().map(PostDto::getId).collect(Collectors.toCollection(LinkedHashSet::new)))
                    .build();
            redisFeedRepository.save(redisFeed);
        }
    }
}