package faang.school.postservice.service.impl;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.mapper.post.CacheablePostMapper;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.repository.post.PostCacheRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.FeedService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final CacheablePostMapper cacheablePostMapper;

    @Value("${feed.cache.count}")
    private int cacheCount;

    @Override
    @Transactional
    public void distributePostsToUsersFeeds(PostPublishedEvent event) {
        List<Long> subsIds = event.getSubscribersIds();

        subsIds.forEach(id -> updateFeed(id, event.getPostId()));
    }

    @Override
    @Transactional
    public void addNewComment(CommentPublishedEvent commentEvent) {
        CacheablePost post = getPost(commentEvent.getPostId());

        var comments = Optional.ofNullable(post.getComments())
                .orElse(new ArrayList<>());
        comments.add(commentEvent);
        post.setComments(comments);
        post.incrementComments();

        postCacheRepository.save(post);
    }

    @Override
    public void addNewLike(LikePostEvent likePostEvent) {
        CacheablePost post = getPost(likePostEvent.getPostId());

        post.incrementLikes();

        postCacheRepository.save(post);
    }

    @Retryable(maxAttempts = 5, retryFor = {Exception.class})
    private void updateFeed(long userId, long postId) {
        Feed feed = feedCacheRepository.findById(userId).orElse(new Feed(userId, new LinkedHashSet<>()));

        addNewPostToFeed(feed, postId);
        feedCacheRepository.save(feed);
    }

    private void addNewPostToFeed(Feed feed, long postId) {
        var postIds = feed.getPostIds();
        postIds.add(postId);
        if (postIds.size() > cacheCount) {
            postIds.remove(postIds.iterator().next());
        }

        feed.setPostIds(postIds);
    }

    private CacheablePost getPost(long postId) {
        CacheablePost post = postCacheRepository.findById(postId)
                .orElse(cacheablePostMapper.toCacheablePost(
                        postRepository.getReferenceById(postId)
                ));

        if (post == null) {
            throw new EntityNotFoundException("post with id = " + post + " not found");
        }

        return post;
    }
}
