package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.DataValidationException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.FeedRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import faang.school.postservice.repository.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostRepository redisPostRepository;
    private final UserServiceClient userServiceClient;
    private final PostRepository postRepository;
    private final PostMapper postMapper;

    @Value("${post.news-feed-batch-size}")
    private int newsFeedBatchSize;

    public TreeSet<PostDto> getNewsFeed(Long postId, Long userId) {
        UserDto user = userServiceClient.getUser(userId);
        if (user == null) {
            String errMessage = String.format("User ID: %d not found", userId);
            log.error(errMessage);
            throw new DataValidationException(errMessage);
        }

        FeedRedis feedRedis = redisFeedRepository.getById(userId);
        TreeSet<PostDto> resultPosts = new TreeSet<>(Comparator.comparing(PostDto::getId).reversed());

        long startPost = postId != null ? postId : 1L;

        if (feedRedis == null || feedRedis.getPostIds().isEmpty()) {
            log.info("Feed not found in Redis. Get posts from DB");
            addPostsFromDbToSet(resultPosts, user.getFolloweesIds(), newsFeedBatchSize, startPost);
            return resultPosts;
        }

        TreeSet<Long> postIds = feedRedis.getPostIds();
        List<PostRedis> redisPosts;

        log.info("Get posts from Redis start with post ID: {}", startPost);
        long finalStartPost = startPost;
        redisPosts = postIds.stream()
                .filter(id -> id >= finalStartPost)
                .limit(newsFeedBatchSize)
                .map(redisPostRepository::findById)
                .map(optPost -> optPost.orElse(null))
                .filter(Objects::nonNull)
                .toList();

        if (redisPosts.size() < newsFeedBatchSize) {
            if (redisPosts.isEmpty()) {
                log.info("Not cached posts in Redis. Get all posts from DB");
                addPostsFromDbToSet(resultPosts, user.getFolloweesIds(), newsFeedBatchSize, startPost);
            } else {
                startPost = redisPosts.get(redisPosts.size() - 1).getId();
                log.info("Not enough posts in Redis. Get posts from DB. Start from post ID: {}", startPost);
                addPostsFromDbToSet(resultPosts, user.getFolloweesIds(), newsFeedBatchSize - redisPosts.size(), startPost);
            }
        }

        resultPosts.addAll(postMapper.fromRedisToListDto(redisPosts));
        return resultPosts;
    }

    private void addPostsFromDbToSet(TreeSet<PostDto> posts, List<Long> authors, int countPosts, long startPostId) {
        List<Post> dbPosts = postRepository.findByAuthorsAndLimitAndStartFromPostId(authors, countPosts, startPostId);
        posts.addAll(postMapper.toListDto(dbPosts));
    }
}
