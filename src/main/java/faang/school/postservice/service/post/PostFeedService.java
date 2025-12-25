package faang.school.postservice.service.post;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.post.PostV2Dto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.feed.FeedDto;
import faang.school.postservice.feed.PostFeedItemDto;
import faang.school.postservice.mapper.PostFeedMapper;
import faang.school.postservice.mapper.PostV2Mapper;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.repository.RedisFeedRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostFeedService {
    private static final int DEFAULT_FEED_LIMIT = 20;

    private final RedisFeedRepository redisFeedRepository;
    private final PostRepository postRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;
    private final PostFeedMapper postFeedMapper;

    @Value("${feed.batch-size:20}")
    private int feedBatchSize;

    @Transactional(readOnly = true)
    public FeedDto getFeed(String lastPostId, Integer limit) {
        long userId = userContext.getUserId();
        int batchSize = limit != null ? limit : DEFAULT_FEED_LIMIT;

        List<String> postIds = redisFeedRepository.getUserFeed(userId, lastPostId, batchSize);

        if (postIds.size() < batchSize) {
            int remaining = batchSize - postIds.size();
            List<Long> dbPostIds = fetchPostsFromDb(userId, lastPostId, remaining);
            postIds.addAll(dbPostIds.stream()
                    .map(Object::toString)
                    .toList());
        }

        List<Long> postIdsLong = postIds.stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<PostFeedItemDto> feedItems = enrichPosts(postIdsLong);

        String nextCursor = !feedItems.isEmpty() && feedItems.size() >= batchSize
                ? feedItems.get(feedItems.size() - 1).id().toString()
                : null;

        return FeedDto.builder()
                .posts(feedItems)
                .nextCursor(nextCursor)
                .hasMore(nextCursor != null)
                .build();
    }

    private List<PostFeedItemDto> enrichPosts(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<PostV2Dto> postsFromRedis = redisFeedRepository.getPostsFromRedis(postIds);
        Map<Long, PostV2Dto> redisPostsMap = postsFromRedis.stream()
                .collect(Collectors.toMap(PostV2Dto::id, Function.identity()));

        List<Long> missingPostIds = postIds.stream()
                .filter(id -> !redisPostsMap.containsKey(id))
                .collect(Collectors.toList());

        Map<Long, PostV2Dto> dbPostsMap = fetchPostsFromDb(missingPostIds).stream()
                .collect(Collectors.toMap(PostV2Dto::id, Function.identity()));

        Map<Long, PostV2Dto> allPosts = new java.util.HashMap<>(redisPostsMap);
        allPosts.putAll(dbPostsMap);

        return postIds.stream()
                .map(allPosts::get)
                .filter(Objects::nonNull)
                .map(post -> {
                    try {
                        UserDto author = userServiceClient.getUser(post.authorId());
                        return postFeedMapper.toFeedItemDto(post, author);
                    } catch (Exception e) {
                        log.warn("Failed to fetch user {}: {}", post.authorId(), e.getMessage());
                        return postFeedMapper.toFeedItemDto(post, createStubUser(post.authorId()));
                    }
                })
                .collect(Collectors.toList());
    }

    private UserDto createStubUser(Long userId) {
        return UserDto.builder()
                .id(userId)
                .username("User " + userId)
                .email("user" + userId + "@example.com")
                .build();
    }

    private List<PostV2Dto> fetchPostsFromDb(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return new ArrayList<>();
        }

        return postRepository.findAllById(postIds).stream()
                .map(PostV2Mapper::toDtoBasic)
                .collect(Collectors.toList());
    }

    private List<Long> fetchPostsFromDb(long userId, String lastPostId, int limit) {

        return postRepository.findPublishedPostsAfterCursor(
                lastPostId != null ? Long.parseLong(lastPostId) : null,
                limit
        );
    }
}
