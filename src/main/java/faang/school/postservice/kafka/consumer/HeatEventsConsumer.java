package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.post.PostDto;
import faang.school.postservice.redis.service.RedisPostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeatEventsConsumer {
    private final RedisPostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.heat:heat}")
    void listener(PostDto event){
        postCacheService.savePostCache(event);
    }
}
