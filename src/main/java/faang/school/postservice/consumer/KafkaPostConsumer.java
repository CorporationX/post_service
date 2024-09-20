package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import faang.school.postservice.service.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostConsumer {
    private final FeedService feedService;

    @KafkaListener(topics = "${spring.kafka.topic.new-post}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void listen(NewPostEvent event) {
        log.info("New post event received: {}", event);
        feedService.addPostToFollowers(event);
    }
}
