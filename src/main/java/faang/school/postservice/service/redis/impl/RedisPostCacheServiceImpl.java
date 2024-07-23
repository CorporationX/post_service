package faang.school.postservice.service.redis.impl;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.mapper.redis.RedisPostMapper;
import faang.school.postservice.model.redis.RedisPost;
import faang.school.postservice.repository.redis.RedisPostRepository;
import faang.school.postservice.service.redis.RedisPostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisKeyValueTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisPostCacheServiceImpl implements RedisPostCacheService {

    @Value("${spring.data.redis.cache.ttl}")
    private int postTtl;

    private final RedisPostRepository redisPostRepository;
    private final RedisPostMapper redisPostMapper;
    private final RedisKeyValueTemplate redisTemplate;

    @Override
    public RedisPost save(PostDto postDto) {
        RedisPost redisPost = redisPostMapper.toEntity(postDto);
        redisPost.setTtl(postTtl);

        redisPostRepository.findById(postDto.getId()).ifPresentOrElse(
                post -> redisTemplate.update(redisPost),
                () -> redisPostRepository.save(redisPost)
        );
        return redisPost;
    }

    @Override
    public Optional<RedisPost> get(long postId) {
        return redisPostRepository.findById(postId);
    }

    @Override
    public void deletePostById(long postId) {
        redisPostRepository.deleteById(postId);
    }
}
