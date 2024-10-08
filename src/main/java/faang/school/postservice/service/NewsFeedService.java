package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.redis.NewsFeedRedisRepository;
import faang.school.postservice.repository.redis.PostRedisRepository;
import faang.school.postservice.repository.redis.UserRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsFeedService {
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final NewsFeedRedisRepository newsFeedRedisRepository;
    private final PostMapper postMapper;

    @Value("${news-feed.batch-size}")
    private int batchSize;
    @Value("${spring.data.redis.cache.news-feed.prefix}")
    private String newsFeedPrefix;

    public List<PostRedis> getNewsFeed(Long userId, Long lastPostId) {
        log.info("Getting news feed for user {}", userId);
        String key = newsFeedPrefix + userId;
        List<Long> postIds = newsFeedRedisRepository.getSortedPostIds(key);
        if (postIds.isEmpty()) {
            return getPostsFromDB(userId, lastPostId, batchSize);
        }
        List<Long> resultPostIds = getResultPostIds(lastPostId, postIds);
        List<PostRedis> result = getPostsFromRedis(resultPostIds);
        if (result.size() < resultPostIds.size()) {
            result = addExpiredPostsAndGet(resultPostIds, result);
        }
        if (result.size() < batchSize) {
            getExtraPostsFromDB(userId, result);
        }
        setAuthors(result);
        return result;
    }

    private List<PostRedis> getPostsFromDB(Long userId, Long lastPostId, int postsCount) {
        log.info("Getting posts from DB");
        List<Long> followeeIds = userServiceClient.getUser(userId).getFolloweesIds();
        List<Post> posts;
        if (lastPostId == null) {
            posts = postRepository.findByAuthors(followeeIds, postsCount);
        } else {
            posts = postRepository.findByAuthorsBeforeId(followeeIds, lastPostId, postsCount);
        }
        return postMapper.toRedis(posts);
    }

    private List<Long> getResultPostIds(Long lastPostId, List<Long> postIds) {
        if (lastPostId == null) {
            return getSubList(postIds, 0L, batchSize);
        } else {
            return getSubList(postIds, lastPostId, batchSize);
        }
    }

    private List<Long> getSubList(List<Long> list, long lastPostId, int batchSize) {
        int startIndex = list.indexOf(lastPostId) + 1;
        int endIndex = Math.min(startIndex + batchSize, list.size());
        return list.subList(startIndex, endIndex);
    }

    private List<PostRedis> getPostsFromRedis(List<Long> resultPostIds) {
        Iterable<PostRedis> postsRedisIterable = postRedisRepository.findAllById(resultPostIds);
        return StreamSupport.stream(postsRedisIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    private List<PostRedis> addExpiredPostsAndGet(List<Long> redisPostIds, List<PostRedis> result) {
        log.info("Adding posts, that were not found in cache");
        Set<PostRedis> postsSet = new TreeSet<>(Comparator.comparing(PostRedis::getId).reversed());
        postsSet.addAll(result);

        List<Long> resultIds = result.stream()
                .map(PostRedis::getId)
                .toList();
        redisPostIds.removeAll(resultIds);

        List<Post> expiredPosts = postRepository.findAllById(redisPostIds);
        postsSet.addAll(postMapper.toRedis(expiredPosts));
        return new ArrayList<>(postsSet);
    }

    private void getExtraPostsFromDB(Long userId, List<PostRedis> result) {
        log.info("Getting extra posts from DB for user {} because feed size is {}", userId, result.size());
        Long lastPostId = result.get(result.size() - 1).getId();
        int postsCount = batchSize - result.size();
        result.addAll(getPostsFromDB(userId, lastPostId, postsCount));
    }

    private void setAuthors(List<PostRedis> posts) {
        log.info("Setting authors to posts");
        Set<Long> userIds = posts.stream()
                .map(post -> post.getAuthor().getId())
                .collect(Collectors.toSet());
        Map<Long, UserRedis> usersRedis = getUsersFromRedis(userIds);
        if (usersRedis.size() < userIds.size()) {
            addExpiredAuthors(usersRedis, userIds);
        }
        posts.forEach(post -> post.setAuthor(usersRedis.get(post.getAuthor().getId())));
    }

    private Map<Long, UserRedis> getUsersFromRedis(Set<Long> userIds) {
        Iterable<UserRedis> userRedisIterable = userRedisRepository.findAllById(userIds);
        return StreamSupport.stream(userRedisIterable.spliterator(), false)
                .collect(Collectors.toMap(UserRedis::getId, user -> user));
    }

    private void addExpiredAuthors(Map<Long, UserRedis> usersRedis, Set<Long> userIds) {
        log.info("Adding authors, that were not found in cache");
        List<Long> userRedisIds = usersRedis.keySet().stream().toList();
        List<Long> expiredUserIds = new ArrayList<>(userIds);
        expiredUserIds.removeAll(userRedisIds);
        List<UserDto> expiredUsers = userServiceClient.getUsersByIds(expiredUserIds);
        expiredUsers.forEach(userDto -> usersRedis.put(
                userDto.getId(), new UserRedis(userDto.getId(), userDto.getUsername())));
    }
}
