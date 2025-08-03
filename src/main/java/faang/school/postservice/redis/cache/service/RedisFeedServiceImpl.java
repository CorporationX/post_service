package faang.school.postservice.redis.cache.service;

import faang.school.postservice.config.redis.RedisProperties;
import faang.school.postservice.dto.post.PostOutputDto;
import faang.school.postservice.redis.cache.model.RedisFeed;
import faang.school.postservice.redis.cache.model.RedisPost;
import faang.school.postservice.redis.cache.repository.RedisFeedRepository;
import faang.school.postservice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisFeedServiceImpl implements RedisFeedService {
    @Value("${default-pagination-pagesize}")
    private final int PAGE_SIZE;

    private final RedisFeedRepository redisFeedRepository;
    private final RedisPostService redisPostService;
    private final RedisProperties redisProperties;
    private final PostService postService;

    @Override
    public RedisFeed getFeed(Long userId, Long lastPostId, Integer pageSize) {
        if (pageSize == null || pageSize <= 0) {
            pageSize = PAGE_SIZE;
        }
        RedisFeed redisFeed = redisFeedRepository.findById(userId).orElse(null);

        if (redisFeed == null || redisFeed.getPosts() == null || redisFeed.getPosts().isEmpty()) {
            TreeSet<RedisPost> postsFromDb = fetchPostsFromDb(userId, lastPostId, pageSize);
            return new RedisFeed(redisProperties.getTimeToLive(), userId, postsFromDb);
        }

        TreeSet<RedisPost> redisPosts = redisFeed.getPosts();
        SortedSet<RedisPost> paginatedPosts;

        if (lastPostId == null) {
            paginatedPosts = redisPosts.stream()
                    .limit(pageSize)
                    .collect(Collectors.toCollection(TreeSet::new));
        } else {
            RedisPost lastPost = redisPostService.findByPostId(lastPostId);
            if (lastPost == null) {
                paginatedPosts = redisPosts.stream()
                        .limit(pageSize)
                        .collect(Collectors.toCollection(TreeSet::new));
            } else {
                paginatedPosts = redisPosts.tailSet(lastPost, false).stream()
                        .limit(pageSize)
                        .collect(Collectors.toCollection(TreeSet::new));
            }
        }

        if (paginatedPosts.size() < pageSize) {
            int remaining = pageSize - paginatedPosts.size();
            TreeSet<RedisPost> postsFromDb = fetchPostsFromDb(userId, lastPostId, remaining);
            paginatedPosts.addAll(postsFromDb);
        }

        return new RedisFeed(redisProperties.getTimeToLive(), userId, new TreeSet<>(paginatedPosts));
    }

    private TreeSet<RedisPost> fetchPostsFromDb(Long userId, Long lastPostId, int limit) {
        List<PostOutputDto> postsDto;

        if (lastPostId == null) {
            postsDto = postService.fetchLatestPublishedPosts(userId, limit);
        } else {
            RedisPost lastRedisPost = redisPostService.findByPostId(lastPostId);
            if (lastRedisPost == null || lastRedisPost.getPublishedAt() == null) {
                postsDto = postService.fetchLatestPublishedPosts(userId, limit);
            } else {
                postsDto = postService.fetchPublishedPostsBefore(userId, lastRedisPost.getPublishedAt(), limit);
            }
        }

        return postsDto.stream()
                .map(dto -> RedisPost.builder()
                        .id(dto.getId())
                        .authorId(dto.getAuthorId())
                        .content(dto.getContent())
                        .createdAt(dto.getCreatedAt())
                        .updatedAt(dto.getUpdatedAt())
                        .publishedAt(dto.getPublishedAt())
                        .likeIds(dto.getLikeIds())
                        .commentIds(dto.getCommentIds())
                        .projectId(dto.getProjectId())
                        .timeToLive(redisProperties.getTimeToLive())
                        .build()
                )
                .collect(Collectors.toCollection(TreeSet::new));
    }

}
