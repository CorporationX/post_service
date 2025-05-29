package faang.school.postservice.service;

import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.PostResponseDto;
import faang.school.postservice.dto.feed.CommentRedisEvent;
import faang.school.postservice.dto.feed.PostFeedResponse;
import faang.school.postservice.dto.redis.PostRedisDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.exception.UserServiceConnectionException;
import faang.school.postservice.mapper.CommentMapper;
import faang.school.postservice.mapper.PostMapper;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedService {

    private static final Long DEFAULT_RETURNING_VALUE = -1L;
    private static final int RETRY_DELAY = 500;
    private static final int RETRY_MULTIPLIER = 3;

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

    @Value("${spring.data.redis.object-cache-options.returning-posts-count}")
    private int returningPostsCount;

    @Value("${spring.data.redis.object-cache-options.comments-count}")
    private int returningCommentsCount;

    public List<PostFeedResponse> getFeed(Long postId) {
        Long userId = userContext.getUserId();
        List<Long> postIds = feedRedisRepository.getFeed(userId, returningPostsCount, postId);
        List<PostFeedResponse> feed = new ArrayList<>();

        postIds.forEach(id -> {
            PostFeedResponse postFeedResponse;

            PostRedisDto post = postRedisRepository.findById(id);
            if (post == null) {
                PostResponseDto postResponse = postService.getPost(id, userId);
                postFeedResponse = postMapper.responseToFeedResponse(postResponse);
                postRedisRepository.savePost(postMapper.toRedisDto(postResponse));
            } else {
                postFeedResponse = postMapper.redisEventToFeedResponse(post);
            }

            Integer likeCount = postRedisRepository.getLikes(id);
            if (likeCount == DEFAULT_RETURNING_VALUE.intValue()) {
                int likeCountOnBase = likeService.getCountLikesOnPost(id);
                postFeedResponse.setLikeCount(likeCountOnBase);
                postRedisRepository.addPostLikes(id, likeCountOnBase);
            } else {
                postFeedResponse.setLikeCount(likeCount);
            }

            Long viewCount = postRedisRepository.getViews(id);
            if (viewCount.equals(DEFAULT_RETURNING_VALUE)) {
                long viewCountOnBase = postService.getPostViewCount(id);
                postFeedResponse.setViewCount(viewCountOnBase);
                postRedisRepository.addPostViews(id, viewCountOnBase);
            } else {
                postFeedResponse.setViewCount(viewCount);
            }

            List<CommentRedisEvent> comments = postRedisRepository.getComments(id);
            if (comments.isEmpty()) {
                postFeedResponse.setComments(commentService.sendCommentsEventByPostId(postId, returningCommentsCount));
            } else {
                postFeedResponse.setComments(commentMapper.eventListToDtoList(comments));
            }

            UserDto author = userRedisRepository.findById(postFeedResponse.getAuthorId());
            if (author == null) {
                author = getPostAuthor(postFeedResponse.getAuthorId());
                postFeedResponse.setAuthor(author);
                userRedisRepository.saveUser(author);
            } else {
                postFeedResponse.setAuthor(author);
            }

            feed.add(postFeedResponse);
        });
        return feed;
    }

    @Retryable(
            retryFor = UserServiceConnectionException.class,
            backoff = @Backoff(delay = RETRY_DELAY, multiplier = RETRY_MULTIPLIER)
    )
    private UserDto getPostAuthor(Long authorId) {
        try {
            return userClient.getUser(authorId);
        } catch (FeignException e) {
            throw new UserServiceConnectionException("User server returned an error: " + e.getMessage());
        }
    }
}
