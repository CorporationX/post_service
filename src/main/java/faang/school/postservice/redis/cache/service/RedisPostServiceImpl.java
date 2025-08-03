package faang.school.postservice.redis.cache.service;

import faang.school.postservice.redis.cache.model.RedisPost;
import faang.school.postservice.redis.cache.repository.RedisPostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisPostServiceImpl implements RedisPostService {
    private final RedisPostRepository redisPostRepository;


    @Override
    public void savePost(RedisPost post) {
        redisPostRepository.save(post);
    }

    @Override
    public RedisPost findByPostId(Long postId) {
        return redisPostRepository.findById(postId).orElseThrow(() -> {
            log.error("Post with id {} not found in Redis cache", postId);
            return new EntityNotFoundException(String.format("Post with id %s not found", postId));
        });
    }
}
