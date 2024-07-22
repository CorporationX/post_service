package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.FeedPublicationDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.dto.user.UserDto;
import faang.school.postservice.mapper.FeedPublicationMapper;
import faang.school.postservice.redis.cache.entity.CommentCache;
import faang.school.postservice.redis.cache.entity.FeedCache;
import faang.school.postservice.redis.cache.entity.PostCache;
import faang.school.postservice.redis.cache.service.feed.FeedCacheService;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.comment.async.AsyncCommentFeedService;
import faang.school.postservice.service.feed.post.async.AsyncPostFeedService;
import faang.school.postservice.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedCacheService feedCacheService;
    private final AsyncCommentFeedService asyncCommentFeedService;
    private final AsyncPostFeedService asyncPostFeedService;
    private final PostRepository postRepository;
    private final UserContext userContext;
    private final UserService userService;
    private final FeedPublicationMapper feedPublicationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<FeedPublicationDto> getNewsFeed(long userId, Pageable pageable) {

        UserDto user = userService.getUserById(userId);
        List<FeedPublicationDto> cachePosts = getFromRedisCache(userId, pageable);

        if (cachePosts != null) {
            return cachePosts;
        }

        return getFromDataBase(user.getSubscriberIds(), pageable);
    }

    private List<FeedPublicationDto> getFromRedisCache(long userId, Pageable pageable) {

        FeedCache feedRedis = feedCacheService.findByUserId(userId);

        if (feedRedis == null || feedRedis.getPosts().isEmpty()) {
            return null;
        }

        NavigableSet<PostCache> posts = feedRedis.getPosts();

        if (pageable.getOffset() + pageable.getPageSize() > posts.size()) {
            return null;
        }

        List<PostCache> caches = getPage(posts, pageable);
        List<FeedPublicationDto> out = new ArrayList<>();

        for (PostCache cache : caches) {
            FeedPublicationDto post = getFeedPublicationFromCache(cache);
            out.add(post);
        }

        return out;
    }

    private FeedPublicationDto getFeedPublicationFromCache(PostCache cache) {

        for (CommentCache commentCache : cache.getComments()) {
            if (commentCache.getAuthor() == null) {
                return getFeedPublicationFromDataBase(cache.getId());
            }
        }

        if (cache.getAuthor() == null) {
            return getFeedPublicationFromDataBase(cache.getId());
        }

        return feedPublicationMapper.toDto(cache);
    }

    private List<FeedPublicationDto> getFromDataBase(List<Long> subscriberIds, Pageable pageable) {

        return postRepository.findFeedPostIdsByUserId(subscriberIds, pageable).stream()
                .map(this::getFeedPublicationFromDataBase)
                .toList();
    }

    private FeedPublicationDto getFeedPublicationFromDataBase(long postId) {

        long currentUserId = userContext.getUserId();
        CompletableFuture<PostFeedDto> post = asyncPostFeedService.getPostsWithAuthor(postId, currentUserId);
        CompletableFuture<List<CommentFeedDto>> comments = asyncCommentFeedService.getCommentsWithAuthors(postId, currentUserId);

        return post
                .thenCombine(comments, feedPublicationMapper::toDto)
                .join();
    }

    private List<PostCache> getPage(NavigableSet<PostCache> posts, Pageable pageable) {

        return posts.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
    }
}
