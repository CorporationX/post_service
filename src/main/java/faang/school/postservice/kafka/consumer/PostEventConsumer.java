package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.post.PostPublishedEvent;
import faang.school.postservice.kafka.event.post.PostViewedEvent;
import faang.school.postservice.cache.service.NewsFeedService;
import faang.school.postservice.cache.service.PostRedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PostEventConsumer {
    private final NewsFeedService newsFeedService;
    private final PostRedisService postRedisService;

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.post.published}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        Long postId = event.getPostId();
        for (Long followerId : event.getFollowerIds()) {
            newsFeedService.addPostConcurrent(followerId, postId);
        }
        ack.acknowledge();
    }

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.post.viewed}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(PostViewedEvent event, Acknowledgment ack) {
        log.info("Received {}", event.toString());
        postRedisService.updateViewsConcurrent(event.getPostId(), event.getCurrentViews());
        ack.acknowledge();
    }
}
