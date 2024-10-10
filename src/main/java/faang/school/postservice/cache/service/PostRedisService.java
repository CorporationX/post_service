package faang.school.postservice.cache.service;

import faang.school.postservice.cache.model.CommentRedis;
import faang.school.postservice.cache.model.PostRedis;
import faang.school.postservice.cache.repository.PostRedisRepository;
import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
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
        return postRedisRepository.findById(id).orElse(null);
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

    public void updateViewsConcurrent(Long postId, Long views) {
        String key = generateKey(postId);
        if (!existsById(postId)) {
            log.info("{} not found in cache", key);
            return;
        }
        concurrentExecutor.execute(key, () -> updateViews(postId, views), "updating views");
    }

    private void updateViews(Long postId, Long views) {
        PostRedis post = findById(postId);
        post.setViews(views);
        postRedisRepository.save(post);
    }

    private void addComment(CommentRedis comment) {
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

    private String generateKey(Long postId) {
        return postPrefix + postId;
    }
}