package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.LikeEvent;
import faang.school.postservice.kafka.service.LikeProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaLikeConsumer {

    private final LikeProcessingService likeProcessingService;

    @KafkaListener(
            topics = "like-event-topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onLikeEvent(LikeEvent event, Acknowledgment ack) {
        try {
            likeProcessingService.addLikeToCache(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw e;
        }
    }
}
