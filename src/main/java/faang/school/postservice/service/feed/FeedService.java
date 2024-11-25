package faang.school.postservice.service.feed;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.post.FeedPostDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.model.CachedFeedDto;
import faang.school.postservice.model.CachedFeedUserDto;
import faang.school.postservice.model.CachedPostDto;
import faang.school.postservice.model.Post;
import faang.school.postservice.repository.redis.RedisFeedRepository;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.repository.redis.RedisUserRepository;
import faang.school.postservice.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final RedisFeedRepository feedRedisRepository;
    private final RedisPostRepository redisPostRepository;
    private final PostService postService;
    private final RedisUserRepository redisUserRepository;
    private final UserServiceClient userServiceClient;

    @Value("${feed.page-size}")
    private Integer pageSize;

    public List<FeedPostDto> loadNextPosts(Long userId, Long startPostId) {
        CachedFeedDto feed = findFeedByUserId(userId);
        List<Long> postIds = getPostIdBatch(feed, startPostId);

        return getAllPostsById(postIds);
    }

    private CachedFeedDto findFeedByUserId(Long userId) {
        return feedRedisRepository.findByUserId(userId)
                .orElseGet(() -> {
                    log.warn("Feed not found for userId: {}, creating empty feed.", userId);
                    return new CachedFeedDto(userId, new TreeSet<>());
                });
    }

    private List<Long> getPostIdBatch(CachedFeedDto feed, Long startPostId) {
        if (feed.getPostsIds().isEmpty()) {
            log.warn("No posts available in feed for userId: {}", feed.getUserId());
            return List.of();
        }

        return startPostId != null
                ? feed.getPostsIds().tailSet(startPostId).stream().limit(pageSize).toList()
                : feed.getPostsIds().stream().limit(pageSize).toList();
    }

    private List<FeedPostDto> getAllPostsById(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<FeedPostDto> feedPostList = new ArrayList<>(getCachedPosts(ids));

        int remainingPostsCount = ids.size() - feedPostList.size();
        if (remainingPostsCount > 0) {
            feedPostList.addAll(fetchMissingPosts(ids, feedPostList));
        }

        return feedPostList.stream().distinct().toList();
    }

    private List<FeedPostDto> getCachedPosts(List<Long> postIdBatch) {
        return StreamSupport.stream(redisPostRepository.findAllById(postIdBatch).spliterator(), false)
                .filter(Objects::nonNull)
                .map(this::buildFeedPostFromFeedPostCache)
                .toList();
    }

    private List<FeedPostDto> fetchMissingPosts(List<Long> postIdBatch, List<FeedPostDto> cachedPosts) {
        Set<Long> cachedPostIds = cachedPosts.stream()
                .map(FeedPostDto::getId)
                .collect(Collectors.toSet());

        List<Long> missingPostIds = postIdBatch.stream()
                .filter(postId -> !cachedPostIds.contains(postId))
                .toList();

        return getPostsByIds(missingPostIds);
    }

    private List<FeedPostDto> getPostsByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        List<Post> postList = postService.findAllById(ids);
        List<FeedPostDto> feedPostList = new ArrayList<>();

        postList.forEach(post -> feedPostList.add(buildFeedPostFromPost(post)));

        return feedPostList;
    }

    private String getAuthorNameByAuthorId(Long userId) {
        return redisUserRepository.findById(userId)
                .map(CachedFeedUserDto::getUsername)
                .orElseGet(() -> {
                    try {
                        UserDto user = userServiceClient.getUser(userId);
                        return user != null ? Optional.ofNullable(user.getUsername()).orElse("unknown") : "unknown";
                    } catch (Exception e) {
                        log.error("Failed to fetch user with ID {}: {}", userId, e.getMessage());
                        return "unknown";
                    }
                });
    }

    private FeedPostDto buildFeedPostFromFeedPostCache(CachedPostDto postCache) {
        return FeedPostDto.builder()
                .id(postCache.getId())
                .content(postCache.getContent())
                .authorName(getAuthorNameByAuthorId(postCache.getAuthorId()))
                .likes(postCache.getLikes())
                .build();
    }

    private FeedPostDto buildFeedPostFromPost(Post post) {
        return FeedPostDto.builder()
                .id(post.getId())
                .content(post.getContent())
                .authorName(getAuthorNameByAuthorId(post.getAuthorId()))
                .likes(post.getLikes().size())
                .build();
    }
}