package faang.school.postservice.config.kafka.consumer;

import faang.school.postservice.event.PostViewEventRecord;
import faang.school.postservice.redis.service.PostCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PostViewEventsConsumer {
    private final PostCacheService postCacheService;

    @KafkaListener(topics = "${spring.data.kafka.topic_name.post_views}", groupId = "${spring.data.kafka.consumer.group-id}")
    void listener(PostViewEventRecord event) {
        postCacheService.addPostView(event.postId());
    }
}