package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.event.post.PostPublishedEvent;
import faang.school.postservice.service.NewsFeedService;
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

    @Async
    @KafkaListener(topics = "${spring.kafka.topic.post.published}", groupId = "${spring.kafka.consumer.group-id}")
    public void listener(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received postPublishedEvent [{}]", event.toString());
        Long postId = event.getPostId();
        for (Long followerId : event.getFollowerIds()) {
            newsFeedService.addPostConcurrent(followerId, postId);
        }
        ack.acknowledge();
    }
}
