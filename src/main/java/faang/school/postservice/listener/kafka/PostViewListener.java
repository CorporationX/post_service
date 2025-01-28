package faang.school.postservice.listener.kafka;

import faang.school.postservice.events.LikeEvent;
import faang.school.postservice.events.PostViewEvent;
import faang.school.postservice.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewListener {

    private final CacheService cacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name.post-view}", groupId = "1")
    public void listenPostEvent(PostViewEvent postViewEvent){
        cacheService.addNewViewToPost(postViewEvent);
    }
}
