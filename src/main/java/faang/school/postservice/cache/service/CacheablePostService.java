package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CacheableComment;
import faang.school.postservice.cache.model.CacheablePost;
import faang.school.postservice.cache.model.CacheableUser;
import faang.school.postservice.cache.repository.CacheablePostRepository;
import faang.school.postservice.client.UserServiceClient;
import faang.school.postservice.dto.UserDto;
import faang.school.postservice.exception.EntityNotFoundException;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class CacheablePostService {
    private final CacheablePostRepository cacheablePostRepository;
    private final PostMapper postMapper;
    private final RedisConcurrentExecutor concurrentExecutor;
    private final CommentService commentService;
    private final CacheableUserService cacheableUserService;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    public List<CacheablePost> getAllByIds(Iterable<Long> ids) {
        Iterable<CacheablePost> cacheablePostIterable = cacheablePostRepository.findAllById(ids);
        return StreamSupport.stream(cacheablePostIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void save(Post post) {
        cacheablePostRepository.save(postMapper.toCacheable(post));
    }

    public void saveAll(Iterable<CacheablePost> posts) {
        cacheablePostRepository.saveAll(posts);
    }

    public void updateIfExists(Post updatedPost) {
        if (updatedPost.isPublished()) {
            if (existsById(updatedPost.getId())) {
                CacheablePost cacheablePost = findById(updatedPost.getId());
                cacheablePost.setContent(updatedPost.getContent());
                cacheablePostRepository.save(postMapper.toCacheable(updatedPost));
            }
        }
    }

    public void deleteIfExists(Long id) {
        if (existsById(id)) {
            cacheablePostRepository.deleteById(id);
        }
    }

    public boolean existsById(Long id) {
        return cacheablePostRepository.existsById(id);
    }

    public CacheablePost findById(Long id) {
        return cacheablePostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post by id %s not found in cache".formatted(id)));
    }

    public void addCommentConcurrent(CacheableComment comment) {
        Long postId = comment.getPostId();
        String key = generateKey(postId);
        if (!existsById(postId)) {
            log.info("{} not found in cache", key);
            return;
        }
        concurrentExecutor.execute(key, () -> addComment(comment), "adding comment by id " + comment.getId());
    }

    public void addComment(CacheableComment comment) {
        CacheablePost cacheablePost = findById(comment.getPostId());
        TreeSet<CacheableComment> comments = cacheablePost.getComments();
        if (comments == null) {
            comments = new TreeSet<>();
        }
        comments.add(comment);
        while (comments.size() > commentsMaxSize) {
            log.info("Removing excess comment from post by id {}", comment.getPostId());
            comments.pollLast();
        }
        cacheablePost.setComments(comments);
        cacheablePostRepository.save(cacheablePost);
    }

    public void updateViewsConcurrent(Long postId, Long views) {
        String key = generateKey(postId);
        if (!existsById(postId)) {
            log.info("{} not found in cache", key);
            return;
        }
        concurrentExecutor.execute(key, () -> updateViews(postId, views), "updating views");
    }

    public void updateViews(Long postId, Long views) {
        CacheablePost post = findById(postId);
        post.setViews(views);
        cacheablePostRepository.save(post);
    }

    public void setCommentsFromDB(List<CacheablePost> posts) {
        log.info("Setting comments for posts");
        List<Long> postIds = posts.stream().map(CacheablePost::getId).toList();
        List<CacheableComment> comments = commentService.findLastBatchByPostIds(commentsMaxSize, postIds);
        if (comments.isEmpty()) {
            return;
        }
        Map<Long, TreeSet<CacheableComment>> commentsByPosts = new HashMap<>();
        comments.forEach(comment -> commentsByPosts
                .computeIfAbsent(comment.getPostId(), k -> new TreeSet<>())
                .add(comment));
        posts.forEach(post -> post.setComments(commentsByPosts.get(post.getId())));
    }

    public void setAuthors(TreeSet<CacheablePost> cacheablePosts) {
        log.info("Setting authors to posts");
        Set<Long> authorIds = extractUserIds(cacheablePosts);

        Map<Long, CacheableUser> authors = cacheableUserService.getAllByIds(authorIds).stream()
                .collect(Collectors.toMap(CacheableUser::getId, user -> user));
        if (authors.size() < authorIds.size()) {
            addExpiredAuthors(authors, authorIds);
        }
        setAuthorsToPostsAndComments(cacheablePosts, authors);
    }

    public Set<Long> extractUserIds(TreeSet<CacheablePost> cacheablePosts) {
        Set<Long> userIds = new HashSet<>();
        cacheablePosts.forEach(post -> {
            userIds.add(post.getAuthor().getId());
            TreeSet<CacheableComment> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> userIds.add(comment.getAuthor().getId()));
            }
        });
        return userIds;
    }

    private void setAuthorsToPostsAndComments(TreeSet<CacheablePost> cacheablePosts, Map<Long, CacheableUser> authors) {
        cacheablePosts.forEach(post -> {
            post.setAuthor(authors.get(post.getAuthor().getId()));
            TreeSet<CacheableComment> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> {
                    Long authorId = comment.getAuthor().getId();
                    comment.setAuthor(authors.get(authorId));
                });
            }
        });
    }

    private void addExpiredAuthors(Map<Long, CacheableUser> cacheableUsers, Set<Long> userIds) {
        log.info("Adding authors, that were not found in cache");
        List<Long> cacheableUserIds = cacheableUsers.keySet().stream().toList();
        List<Long> expiredUserIds = new ArrayList<>(userIds);
        expiredUserIds.removeAll(cacheableUserIds);
        List<UserDto> expiredUsers = userServiceClient.getUsersByIds(expiredUserIds);
        expiredUsers.forEach(userDto -> cacheableUsers.put(
                userDto.getId(), new CacheableUser(userDto.getId(), userDto.getUsername())));
    }

    private String generateKey(Long postId) {
        return postPrefix + postId;
    }
}