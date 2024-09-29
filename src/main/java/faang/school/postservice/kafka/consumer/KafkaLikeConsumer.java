package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostEvent;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.service.RedisPostCacheService;
import faang.school.postservice.repository.PostRepository;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {
    private final PostRepository postRepository;
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final PostCacheMapper mapper;
    private final RedisPostCacheService redisPostCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.likes:likes}")
    void listener(PostEvent event){
        incrementLikesInPostCache(event.id());
    }

    private void incrementLikesInPostCache(Long postId){
        if (postCacheRedisRepository.existsById(postId)){
            redisPostCacheService.incrementConcurrentPostLikes(postId);
        } else {
            var postCache = postRepository.findById(postId)
                    .map(mapper::toPostCache)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));

            postCacheRedisRepository.save(postCache);
        }
    }
}