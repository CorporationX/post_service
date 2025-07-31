package faang.school.postservice.kafka.consumer;

import faang.school.postservice.kafka.event.CommentEvent;
import faang.school.postservice.kafka.service.CommentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaCommentConsumer {

    private final CommentProcessingService commentProcessingService;

    @KafkaListener(
            topics = "comment-event-topic",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onCommentEvent(CommentEvent event, Acknowledgment ack) {
        try {
            commentProcessingService.addCommentToCache(event);
            ack.acknowledge();
        } catch (Exception e) {
            throw e;
        }
    }
}
