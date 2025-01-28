package faang.school.postservice.listener.kafka;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LikeEventListener {

    private final CacheService cacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.comments}", groupId = "1")
    public void listenLikeEvent(LikeEvent likeEvent){
        cacheService.addLikeToCommentOrPost(likeEvent);
    }
}
