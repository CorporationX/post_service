package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.model.UserRedis;
import faang.school.postservice.cache.repository.PostRedisRepository;
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
public class PostRedisService {
    private final PostRedisRepository postRedisRepository;
    private final PostMapper postMapper;
    private final RedisConcurrentExecutor concurrentExecutor;
    private final CommentService commentService;
    private final UserRedisService userRedisService;
    private final UserServiceClient userServiceClient;

    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;
    @Value("${spring.data.redis.cache.post.prefix}")
    private String postPrefix;

    public List<PostRedis> getAllByIds(Iterable<Long> ids) {
        Iterable<PostRedis> postRedisIterable = postRedisRepository.findAllById(ids);
        return StreamSupport.stream(postRedisIterable.spliterator(), false)
                .collect(Collectors.toList());
    }

    public void save(Post post) {
        postRedisRepository.save(postMapper.toRedis(post));
    }

    public void saveAll(Iterable<PostRedis> posts) {
        postRedisRepository.saveAll(posts);
    }

    public void updateIfExists(Post updatedPost) {
        if (updatedPost.isPublished()) {
            if (existsById(updatedPost.getId())) {
                PostRedis postRedis = findById(updatedPost.getId());
                postRedis.setContent(updatedPost.getContent());
                postRedisRepository.save(postMapper.toRedis(updatedPost));
            }
        }
    }

    public void deleteIfExists(Long id) {
        if (existsById(id)) {
            postRedisRepository.deleteById(id);
        }
    }

    public boolean existsById(Long id) {
        return postRedisRepository.existsById(id);
    }

    public PostRedis findById(Long id) {
        return postRedisRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post by id %s not found in cache".formatted(id)));
    }

    public void addCommentConcurrent(CommentRedis comment) {
        Long postId = comment.getPostId();
        String key = generateKey(postId);
        if (!existsById(postId)) {
            log.info("{} not found in cache", key);
            return;
        }
        concurrentExecutor.execute(key, () -> addComment(comment), "adding comment by id " + comment.getId());
    }

    public void addComment(CommentRedis comment) {
        PostRedis postRedis = findById(comment.getPostId());
        TreeSet<CommentRedis> comments = postRedis.getComments();
        if (comments == null) {
            comments = new TreeSet<>();
        }
        comments.add(comment);
        while (comments.size() > commentsMaxSize) {
            log.info("Removing excess comment from post by id {}", comment.getPostId());
            comments.pollLast();
        }
        postRedis.setComments(comments);
        postRedisRepository.save(postRedis);
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
        PostRedis post = findById(postId);
        post.setViews(views);
        postRedisRepository.save(post);
    }

    public void setCommentsFromDB(List<PostRedis> posts) {
        log.info("Setting comments for posts");
        List<Long> postIds = posts.stream().map(PostRedis::getId).toList();
        List<CommentRedis> comments = commentService.findLastBatchByPostIds(commentsMaxSize, postIds);
        if (comments.isEmpty()) {
            return;
        }
        Map<Long, TreeSet<CommentRedis>> commentsByPosts = new HashMap<>();
        comments.forEach(comment -> commentsByPosts
                .computeIfAbsent(comment.getPostId(), k -> new TreeSet<>())
                .add(comment));
        posts.forEach(post -> post.setComments(commentsByPosts.get(post.getId())));
    }

    public void setAuthors(TreeSet<PostRedis> postsRedis) {
        log.info("Setting authors to posts");
        Set<Long> authorIds = extractUserIds(postsRedis);

        Map<Long, UserRedis> authors = userRedisService.getAllByIds(authorIds).stream()
                .collect(Collectors.toMap(UserRedis::getId, user -> user));
        if (authors.size() < authorIds.size()) {
            addExpiredAuthors(authors, authorIds);
        }
        setAuthorsToPostsAndComments(postsRedis, authors);
    }

    public Set<Long> extractUserIds(TreeSet<PostRedis> postsRedis) {
        Set<Long> userIds = new HashSet<>();
        postsRedis.forEach(post -> {
            userIds.add(post.getAuthor().getId());
            TreeSet<CommentRedis> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> userIds.add(comment.getAuthor().getId()));
            }
        });
        return userIds;
    }

    private void setAuthorsToPostsAndComments(TreeSet<PostRedis> postsRedis, Map<Long, UserRedis> authors) {
        postsRedis.forEach(post -> {
            post.setAuthor(authors.get(post.getAuthor().getId()));
            TreeSet<CommentRedis> comments = post.getComments();
            if (comments != null) {
                comments.forEach(comment -> {
                    Long authorId = comment.getAuthor().getId();
                    comment.setAuthor(authors.get(authorId));
                });
            }
        });
    }

    private void addExpiredAuthors(Map<Long, UserRedis> usersRedis, Set<Long> userIds) {
        log.info("Adding authors, that were not found in cache");
        List<Long> userRedisIds = usersRedis.keySet().stream().toList();
        List<Long> expiredUserIds = new ArrayList<>(userIds);
        expiredUserIds.removeAll(userRedisIds);
        List<UserDto> expiredUsers = userServiceClient.getUsersByIds(expiredUserIds);
        expiredUsers.forEach(userDto -> usersRedis.put(
                userDto.getId(), new UserRedis(userDto.getId(), userDto.getUsername())));
    }

    private String generateKey(Long postId) {
        return postPrefix + postId;
    }
}