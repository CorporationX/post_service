package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.events.PostViewEvent;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventsConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.kafka.topic-name. post-views:post_views}")
    void listener(PostViewEvent event){
        postCacheService.addPostView(event.postId());
    }
}