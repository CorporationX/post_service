package faang.school.postservice.service.feed;

import faang.school.postservice.config.context.UserContext;
import faang.school.postservice.dto.feed.CommentFeedDto;
import faang.school.postservice.dto.feed.PostFeedDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.PostFeedMapper;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.redis.cache.entity.FeedRedisCache;
import faang.school.postservice.redis.cache.entity.PostRedisCache;
import faang.school.postservice.redis.cache.service.feed.FeedRedisCacheService;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.service.feed.comment.async.AsyncCommentFeedService;
import faang.school.postservice.service.feed.post.async.AsyncPostFeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedRedisCacheService feedRedisCacheService;
    private final AsyncCommentFeedService asyncCommentFeedService;
    private final AsyncPostFeedService asyncPostFeedService;
    private final PostRepository postRepository;
    private final PostMapper postMapper;
    private final PostFeedMapper postFeedMapper;
    private final UserContext userContext;

    @Override
    @Transactional(readOnly = true)
    public List<PostFeedDto> getNewsFeed(long userId, Pageable pageable) {

        List<PostFeedDto> cachePosts = getFromRedisCache(userId, pageable);

        if (cachePosts != null) {
            return cachePosts;
        }

        return getFromDataBase(userId, pageable);
    }

    private List<PostFeedDto> getFromRedisCache(long userId, Pageable pageable) {

        FeedRedisCache feedRedis = feedRedisCacheService.findByUserId(userId);

        if (feedRedis == null || feedRedis.getPosts().isEmpty()) {
            return null;
        }

        NavigableSet<PostRedisCache> posts = feedRedis.getPosts();

        if (pageable.getOffset() + pageable.getPageSize() > posts.size()) {
            return null;
        }

        List<PostRedisCache> caches = getPage(posts, pageable);
        NavigableSet<PostRedisCache> out = new TreeSet<>();

        for (PostRedisCache cache : caches) {

            if (cache.getAuthor() == null) {
                Post post = getPostFromDataBase(cache.getId());
                out.add(postMapper.toRedisCache(post));
            }

            out.add(cache);
        }

        return out.stream()
                .map(postFeedMapper::toDto)
                .toList();
    }

    private List<PostFeedDto> getFromDataBase(long userId, Pageable pageable) {

        return postRepository.findFeedPostIdsByUserId(userId, pageable).stream()
                .map(this::getFeedPostFromDataBase)
                .toList();
    }

    private Post getPostFromDataBase(long postId) {

        return postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post with id " + postId + " not found"));
    }

    private PostFeedDto getFeedPostFromDataBase(long postId) {

        long currentUserId = userContext.getUserId();
        CompletableFuture<PostFeedDto> post = asyncPostFeedService.getPostsWithAuthor(postId, currentUserId);
        CompletableFuture<List<CommentFeedDto>> comments = asyncCommentFeedService.getCommentsWithAuthors(postId, currentUserId);

        return post
                .thenCombine(comments, (p, c) -> {
                    p.setComments(c);
                    return p;
                })
                .join();
    }

    private List<PostRedisCache> getPage(NavigableSet<PostRedisCache> posts, Pageable pageable) {

        return posts.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .toList();
    }
}
