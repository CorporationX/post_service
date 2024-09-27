package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostEvent;
import faang.school.postservice.redis.mapper.PostCacheMapper;
import faang.school.postservice.redis.repository.PostCacheRedisRepository;
import faang.school.postservice.redis.service.RedisPostCacheService;
import faang.school.postservice.repository.PostRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaPostViewConsumer {
    private final PostCacheRedisRepository postCacheRedisRepository;
    private final RedisPostCacheService redisPostCacheService;
    private final PostRepository postRepository;
    private final PostCacheMapper mapper;

    @KafkaListener(topics = "${spring.kafka.topic-name. post-views:post_views}")
    void listener(PostEvent event){
        addPostView(event.postId());
    }

    private void addPostView(Long postId){
        if (postCacheRedisRepository.existsById(postId)) {
            redisPostCacheService.incrementConcurrentPostViews(postId);
        }else {
            var postCache = postRepository.findById(postId)
                    .map(mapper::toPostCache)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found"));
            postCache.setViews(1);
            postCacheRedisRepository.save(postCache);
        }
    }
}