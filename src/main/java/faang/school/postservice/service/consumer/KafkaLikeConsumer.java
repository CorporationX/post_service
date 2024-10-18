package faang.school.postservice.service.consumer;

import faang.school.postservice.dto.event.LikeEvent;
import faang.school.postservice.dto.redisCache.PostCache;
import faang.school.postservice.repository.redisCache.RedisPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeConsumer {

    private final RedisPostRepository redisPostRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    @KafkaListener(topics = "${spring.data.kafka.topics.like.name}")
    public void listenerLikeEvent(LikeEvent event, Acknowledgment acknowledgment){
        Long postId = event.getPostId();
        // TODO: повтор
        redisTemplate.watch("Posts:" + postId);

        try{
            redisPostRepository.findById(postId)
                    .ifPresent(postCache -> {
                        processEvent(event, postCache);

                        redisTemplate.multi();
                        redisPostRepository.save(postCache);

                        if(redisTemplate.exec() != null){
                            acknowledgment.acknowledge();
                        }
                    });
        } finally {
            redisTemplate.unwatch();
        }
    }

    public void processEvent(LikeEvent event, PostCache postCache){
        switch (event.getEventType()){
            case CREATE -> postCache.incNumberLikes();
            case DELETE -> postCache.decNumberLikes();
        }
    }
}
