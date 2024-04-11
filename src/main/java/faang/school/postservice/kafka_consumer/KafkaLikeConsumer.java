package faang.school.postservice.kafka_consumer;

import faang.school.postservice.dto.kafka_events.LikeKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaLikeConsumer extends AbstractKafkaConsumer<LikeKafkaEvent> {

    @KafkaListener(topics = "${spring.kafka.topics.like.name}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment acknowledgment) {
        LikeKafkaEvent likeKafkaEvent = listen(message, LikeKafkaEvent.class);
        feedService.addLikeToPost(likeKafkaEvent, acknowledgment);
        log.info("Received LikeKafkaEvent" + likeKafkaEvent);
    }
}
