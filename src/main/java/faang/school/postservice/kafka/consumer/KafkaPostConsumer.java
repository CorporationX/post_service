package faang.school.postservice.kafka.consumer;

import faang.school.postservice.model.event.PostBySubscribersEvent;
import faang.school.postservice.repository.cache.FeedCacheRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaPostConsumer {
    private final FeedCacheRepository feedCacheRepository;

    @KafkaListener(topics = "${post-service.kafka.post-topic}")
    public void listen(@Payload PostBySubscribersEvent event,
                       @Header(KafkaHeaders.ACKNOWLEDGMENT) Acknowledgment ack) {
        try {
            log.info("Received record from kafka: {}", event);
            ack.acknowledge();
            event.getSubscriberIds().forEach(id -> feedCacheRepository.cacheFeed(id, event.getPostId()));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
