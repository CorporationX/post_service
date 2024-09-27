package faang.school.postservice.consumer;


import faang.school.postservice.events.PostEvent;
import faang.school.postservice.service.redis.FeedService;
import faang.school.postservice.service.redis.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer {

    private final FeedService feedService;
    private final UserCacheService userCacheService;

    @Value(value = "${spring.data.kafka.topic.posts_topic}")
    private String postsTopic;

    @KafkaListener(topics = "postsTopic",
            containerFactory = "postEventKafkaListenerContainerFactory")
    public void listenPostEvent(PostEvent event, Acknowledgment ack) {
        log.info("Received post event: {}", event);
        Long postId = event.getPostId();
        List<Long> followersIds = event.getFollowerIds();
        followersIds.forEach(followersId -> feedService.addPostToFeed(followersId, postId));
        ack.acknowledge();
    }
}
