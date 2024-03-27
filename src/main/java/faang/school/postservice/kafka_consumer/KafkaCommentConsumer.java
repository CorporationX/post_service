package faang.school.postservice.kafka_consumer;

import faang.school.postservice.dto.kafka_events.CommentKafkaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KafkaCommentConsumer extends AbstractKafkaConsumer<CommentKafkaEvent> {

    @KafkaListener(topics = "${spring.kafka.topics.comment.name}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(String message, Acknowledgment acknowledgment) {
        CommentKafkaEvent commentKafkaEvent = listen(message, CommentKafkaEvent.class);
        feedService.addCommentToPost(commentKafkaEvent, acknowledgment);
        log.info("Received CommentKafkaEvent" + commentKafkaEvent);
    }
}
