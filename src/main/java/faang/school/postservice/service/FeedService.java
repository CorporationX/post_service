package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import faang.school.postservice.dto.feed.PostFeedResponse;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserServiceConnectionException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.publisher.AuthorRequestEventPublisher;
import faang.school.postservice.repository.FeedRedisRepository;
import faang.school.postservice.repository.PostRedisRepository;
import faang.school.postservice.repository.UserRedisRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;
    private static final String UNKNOWN_USER = "unknown user";

    private final UserContext userContext;
    private final FeedRedisRepository feedRedisRepository;
    private final PostRedisRepository postRedisRepository;
    private final UserRedisRepository userRedisRepository;
    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;
    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final UserServiceClient userClient;
    private final AuthorRequestEventPublisher authorRequestEventPublisher;

    @Value("${spring.data.redis.object-cache-options.returning-posts-count}")
    private int returningPostsCount;

    @Value("${spring.data.redis.object-cache-options.comments-count}")
    private int returningCommentsCount;

    public List<PostFeedResponse> getFeed(Long postId) {
        Long userId = userContext.getUserId();
        List<Long> postIds = feedRedisRepository.getFeed(userId, returningPostsCount, postId);

        Map<Long, PostRedisDto> postsOnRedis = postRedisRepository.findByIds(postIds);
        List<PostFeedResponse> feed = postIds.stream()
                .map(postsOnRedis::get)
                .map(postMapper::redisEventToFeedResponse)
                .collect(Collectors.toCollection(ArrayList::new));

        List<Long> missingPostIds = findMissingIds(postsOnRedis, postIds);
        if (!missingPostIds.isEmpty()) {
            List<PostResponseDto> postsOnDatabase = postService.getPostsByIds(missingPostIds);
            List<PostFeedResponse> postsResponse = postMapper.responsesToFeedResponses(postsOnDatabase);
            feed.addAll(postsResponse);
            feed.sort(Comparator.comparing(PostFeedResponse::getPublishedAt).reversed());

            List<PostRedisDto> postRedisDtoList = postMapper.toRedisDtoList(postsOnDatabase);
            postRedisRepository.savePostsBatch(postRedisDtoList);
        }

        Map<Long, Integer> likes = postRedisRepository.getLikesByIds(postIds);

        List<Long> missingLikeIds = findMissingIds(likes, postIds);
        if (!missingLikeIds.isEmpty()) {
            Map<Long, Integer> countLikesOnDatabase = likeService.getCountsLikesByPostIds(missingLikeIds);

            likes.putAll(countLikesOnDatabase);
            postRedisRepository.addPostLikesBatch(countLikesOnDatabase);
        }

        Map<Long, Long> views = postRedisRepository.getViewsByIds(postIds);

        List<Long> missingViewIds = findMissingIds(views, postIds);
        if (!missingViewIds.isEmpty()) {
            Map<Long, Long> countViewsOnDatabase = postService.getCountViewsByPostIds(missingViewIds);

            views.putAll(countViewsOnDatabase);
            postRedisRepository.addPostViewsBatch(countViewsOnDatabase);
        }

        Map<Long, List<CommentRedisEvent>> commentsOnRedis = postRedisRepository.getCommentsByIds(postIds);
        Map<Long, List<CommentDto>> comments = commentsOnRedis.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().stream()
                                .map(commentMapper::eventToDto)
                                .toList()
                ));

        List<Long> missingCommentIds = findMissingIds(commentsOnRedis, postIds);
        if (!missingCommentIds.isEmpty()) {
            Map<Long, List<CommentDto>> commentsOnDatabase =
                    commentService.getCommentsByPostIds(missingCommentIds, returningCommentsCount);
            comments.putAll(commentsOnDatabase);

            commentsOnDatabase.forEach((key, value) -> commentService.sendCommentsEvent(value));
        }

        List<Long> authorIds = feed.stream()
                .map(PostFeedResponse::getAuthorId)
                .toList();
        Map<Long, UserDto> users = userRedisRepository.findByIds(authorIds);

        List<Long> missingUserIds = findMissingIds(users, postIds);
        if (!missingUserIds.isEmpty()) {
            List<UserDto> usersOnDatabase = getPostAuthors(missingUserIds);
            users.putAll(usersOnDatabase.stream()
                    .collect(Collectors.toMap(UserDto::id, user -> user)));

            usersOnDatabase.forEach(user -> authorRequestEventPublisher.publish(user.id()));
        }

        feed.forEach(post -> {
            Long id = post.getId();
            Long authorId = post.getAuthorId();

            post.setLikeCount(likes.getOrDefault(id, 0));
            post.setViewCount(views.getOrDefault(id, 0L));
            post.setComments(comments.getOrDefault(id, Collections.emptyList()));
            post.setAuthor(users.getOrDefault(authorId, UserDto.builder().username(UNKNOWN_USER).build()));
        });

        return feed;
    }

    @Retryable(
            retryFor = UserServiceConnectionException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    private List<UserDto> getPostAuthors(List<Long> authorIds) {
        try {
            return userClient.getUsersByIds(authorIds);
        } catch (FeignException e) {
            throw new UserServiceConnectionException("User server returned an error: " + e.getMessage());
        }
    }

    private List<Long> findMissingIds(Map<Long, ?> mapForFound, List<Long> allIds) {
        Set<Long> foundIds = mapForFound.keySet();
        return allIds.stream()
                .filter(id -> !foundIds.contains(id))
                .toList();
    }
}
