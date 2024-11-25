package faang.school.postservice.service.impl;

import faang.school.postservice.dto.comment.CommentPublishedEvent;
import faang.school.postservice.dto.like.LikePostEvent;
import faang.school.postservice.dto.post.PostPublishedEvent;
import faang.school.postservice.dto.post.PostViewEvent;
import faang.school.postservice.mapper.UserMapper;
import faang.school.postservice.mapper.post.CacheablePostMapper;
import faang.school.postservice.model.CacheableUser;
import faang.school.postservice.model.Feed;
import faang.school.postservice.model.User;
import faang.school.postservice.model.post.CacheablePost;
import faang.school.postservice.repository.FeedCacheRepository;
import faang.school.postservice.repository.UserCacheRepository;
import faang.school.postservice.repository.UserRepository;
import faang.school.postservice.repository.post.PostCacheRepository;
import faang.school.postservice.repository.post.PostRepository;
import faang.school.postservice.service.FeedService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedCacheRepository feedCacheRepository;
    private final PostCacheRepository postCacheRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CacheablePostMapper cacheablePostMapper;
    private final UserMapper userMapper;
    private final UserCacheRepository userCacheRepository;

    @Value("${feed.cache.count}")
    private int countOfPostsInFeed;

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
    @Transactional
    public void addNewLike(LikePostEvent likePostEvent) {
        CacheablePost post = getPost(likePostEvent.getPostId());

        post.incrementLikes();

        postCacheRepository.save(post);
    }

    @Override
    @Transactional
    public void addNewView(PostViewEvent postViewEvent) {
        CacheablePost post = getPost(postViewEvent.postId());

        post.incrementViews();

        postCacheRepository.save(post);
    }

    @Override
    @Async("feedThreadPool")
    @Transactional
    public void generateFeedForUser(long userId) {
        List<CacheablePost> posts = postRepository.getPostsForFeedByUserId(userId, countOfPostsInFeed).stream()
                .map(cacheablePostMapper::toCacheablePost)
                .toList();

        List<User> users = userRepository.findAllById(
                posts.stream()
                        .map(CacheablePost::getAuthorId)
                        .toList()
        );

        List<CacheableUser> cacheableUsers = users.stream()
                .map(userMapper::toCacheable)
                .toList();

        Feed feed = new Feed(userId, new LinkedHashSet<>(
                posts.stream()
                        .map(CacheablePost::getId)
                        .toList()
        ));

        feedCacheRepository.save(feed);
        postCacheRepository.saveAll(posts);
        userCacheRepository.saveAll(cacheableUsers);

        log.info("feed for user with id = " + userId + " generated");
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
            throw new EntityNotFoundException("post with id = " + postId + " not found");
        }

        return post;
    }
}
