package faang.school.postservice.service.redis;

import faang.school.postservice.dto.comment.LastCommentDto;
import faang.school.postservice.exception.NotFoundException;
import faang.school.postservice.mapper.post.PostMapper;
import faang.school.postservice.model.Post;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.PostCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;

import static faang.school.postservice.converters.CollectionConverter.toList;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostCacheService {
    @Value("${spring.data.redis.cache.post_topic.ttl}")
    private long ttl;
    private final PostCacheRepository postRepository;
    private final RedisTemplate<Long, RedisPost> postRedisTemplate;
    private final PostMapper postMapper;
    private final UserCacheService userCacheService;

    public RedisPost findPostById(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new NotFoundException("Post not found"));
    }

    public List<RedisPost> findPosts(List<Long> ids) {
        return toList(postRepository.findAllById(ids));
    }

    public void addPost(Post post) {
        log.info("publishing post {} to post cache", post.getId());
        RedisPost redisPost = postMapper.toRedisEntity(post);
        String postAuthorName = userCacheService
                .findUserById(post.getAuthorId())
                .getUserInfo()
                .getUsername();

        redisPost.getPostInfoDto().getDto().setUsername(postAuthorName);
        LinkedHashSet<LastCommentDto> lastComments = redisPost.getPostInfoDto().getComments();
        lastComments.stream().map(comment -> {
            String commentAuthorName;
            commentAuthorName = userCacheService
                    .findUserById(comment.getAuthorId())
                    .getUserInfo()
                    .getUsername();
            comment.setAuthor(commentAuthorName);
            return comment;
        });
        redisPost.getPostInfoDto().setComments(lastComments);
        savePost(redisPost);
    }

    private void savePost(RedisPost redisPost) {
        redisPost.setTimeToLive(ttl);
        log.info("saving post {} to post cache with ttl = {}", redisPost.getId(), ttl);
        postRepository.save(redisPost);
        log.info("post {} saved", redisPost.getId());
    }
}
