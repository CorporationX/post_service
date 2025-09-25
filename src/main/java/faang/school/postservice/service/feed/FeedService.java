package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.config.props.FeedProps;
import faang.school.postservice.dto.comment.CommentDto;
import faang.school.postservice.dto.feed.FeedDto;
import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.redis.UserRedis;
import faang.school.postservice.service.cache.FeedCacheService;
import faang.school.postservice.service.cache.PostCacheService;
import faang.school.postservice.service.cache.UserCacheService;
import faang.school.postservice.service.comment.CommentService;
import faang.school.postservice.service.post.PostService;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedCacheService feedCacheService;
    private final UserContext userContext;
    private final PostService postService;
    private final UserService userService;
    private final CommentService commentService;
    private final PostCacheService postCacheService;
    private final UserCacheService userCacheService;
    private final PostMapper postMapper;
    private final FeedProps feedProps;

    public List<FeedDto> getFeed(Long lastPostId, int batch) {
        long userId = userContext.getUserId();
        log.info("Start getting feed for userId = {}", userId);
        Set<Long> postIds = feedCacheService.getNextPosts(userId, lastPostId, batch);
        log.info("Got {}/{} posts from cache for userId = {}", postIds.size(), batch, userId);

        if (postIds.size() < batch) {
            log.info("Try to prepare feed from DB. Cause: not found in cache for userId = {} or found less", userId);
            List<Long> subs = userService.getSubs(userId);
            List<PostDto> posts = postService.findRecentBatchByAuthors(subs, lastPostId, batch);

            if (posts.isEmpty()) {
                posts.addAll(postService.findAnyRecentBatch(batch));
            } else {
                feedCacheService.addPosts(userId, posts);
            }

            return toFeeds(posts);
        }

        return toFeeds(postIds);
    }

    private List<FeedDto> toFeeds(List<PostDto> posts) {
        return posts.stream()
                .map(post -> {
                    List<CommentDto> comments = getComments(post.id());
                    String author = getAuthor(post.authorId(), post.projectId());
                    return buildFeedDto(post, comments, author);
                })
                .toList();
    }

    private List<FeedDto> toFeeds(Set<Long> postIds) {
        return postIds.stream()
                .map(postId -> {
                    PostDto post = getPost(postId);
                    List<CommentDto> comments = getComments(postId);
                    String author = getAuthor(post.authorId(), post.projectId());
                    return buildFeedDto(post, comments, author);
                }).toList();
    }

    private PostDto getPost(long postId) {
        return postCacheService.findById(postId)
                .map(postMapper::toDto)
                .orElseGet(() -> {
                    log.info("Try to get post in DB. Cause: not found in cache by id = {}", postId);
                    PostDto post = postService.getById(postId);
                    postCacheService.save(post);
                    return post;
                });
    }

    private String getAuthor(Long authorId, Long projectId) {
        return userCacheService.findAuthor(authorId, projectId)
                .orElseGet(() -> {
                    log.info("Try to get user from DB. Cause: not found in cache by id = {}, {}", authorId, projectId);
                    UserRedis userRedis = userService.toUserRedis(authorId, projectId);
                    userCacheService.save(userRedis);
                    return userRedis.getUsername();
                });
    }

    private List<CommentDto> getComments(Long postId) {
        List<CommentDto> comments = postCacheService.getComments(postId);
        if (comments.isEmpty()) {
            log.info("Try to get comments from DB. Cause: not found in cache by postId = {}", postId);
            comments = commentService.findNewByPostId(postId, feedProps.comment().limit());
            postCacheService.saveComments(postId, comments);
        }

        return comments;
    }

    private FeedDto buildFeedDto(PostDto post, List<CommentDto> comments, String author) {
        return new FeedDto(
                post.id(),
                post.content(),
                author,
                post.likes(),
                post.views(),
                post.createdAt(),
                comments);
    }
}
