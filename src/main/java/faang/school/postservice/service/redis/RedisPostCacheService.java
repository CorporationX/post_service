package faang.school.postservice.service.redis;

import faang.school.postservice.dto.PostDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RedisPostCacheService {
    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisUserCacheService redisUserCacheService;
    private final RedisKeyValueTemplate redisTemplate;
    @Value("${spring.data.redis.cache.ttl.post}")
    private int postTtl;

    public RedisPost save(PostDto postDto) {
        Long postId = postDto.getId();
        redisUserCacheService.save(postDto.getAuthorId());

        RedisPost redisPost = redisPostMapper.toEntity(postDto);
        redisPost.setTtl(postTtl);

        redisPostRepository.findById(postId).ifPresentOrElse(
                (post) -> redisTemplate.update(redisPost),
                () -> redisPostRepository.save(redisPost)
        );
        return redisPost;
    }

    public void update(long postId, Consumer<RedisPost> consumer) {
        RedisPost redisPost = redisPostRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found"));

        consumer.accept(redisPost);
    }

    public Optional<RedisPost> get (long postId) {
        return redisPostRepository.findById(postId);
    }

    public void deletePostById(long postId) {
        redisPostRepository.deleteById(postId);
    }
}
