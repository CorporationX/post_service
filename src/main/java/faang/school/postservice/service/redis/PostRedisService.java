package faang.school.postservice.service.redis;

import faang.school.postservice.mapper.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.CommentRedis;
import faang.school.postservice.model.redis.PostRedis;
import faang.school.postservice.repository.redis.PostRedisRepository;
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
    @Value("${spring.data.redis.cache.post.comments.max-size}")
    private int commentsMaxSize;

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

    public void addComment(CommentRedis comment) {
        if (!existsById(comment.getPostId())) {
            log.info("Post by id {} not found in cache", comment.getPostId());
            return;
        }
        log.info("Adding comment by id {} to post by id {}", comment.getId(), comment.getPostId());

        PostRedis postRedis = findById(comment.getPostId());
        TreeSet<CommentRedis> comments = postRedis.getComments();
        if (comments == null) {
            comments = new TreeSet<>();
        }
        comments.add(comment);
        while (comments.size() > commentsMaxSize) {
            comments.pollLast();
        }
        postRedis.setComments(comments);
        postRedisRepository.save(postRedis);
    }
}