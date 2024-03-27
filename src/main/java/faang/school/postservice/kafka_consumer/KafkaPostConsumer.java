package faang.school.postservice.kafka_consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.kafka_events.PostKafkaEvent;
import faang.school.postservice.service.cash.FeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaPostConsumer extends AbstractKafkaConsumer<PostKafkaEvent> {
    private final FeedService feedService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${spring.kafka.topics.post.name}", groupId = "${spring.kafka.consumer.group-id}", containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment acknowledgment) {
        PostKafkaEvent postKafkaEvent = listen(message, PostKafkaEvent.class);
        feedService.addToFeed(postKafkaEvent, acknowledgment);
        log.info("Received PostKafkaEvent: " + postKafkaEvent);
    }
}
