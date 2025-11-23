package faang.school.postservice.producer;

import faang.school.postservice.dto.kafka.CommentEventDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentEventProducer {

    @Qualifier("commentEventKafkaTemplate")
    private final KafkaTemplate<String, CommentEventDto> commentEventKafkaTemplate;

    @Value("${kafka.topic.comments}")
    private String topic;

    public void publish(CommentEventDto event) {
        commentEventKafkaTemplate.send(topic, event.commentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Sent CommentEvent [postId={}, commentId={}] to topic={} partition={}",
                                event.postId(),
                                event.commentId(),
                                topic,
                                result.getRecordMetadata().partition());
                    } else {
                        log.error("Failed to send CommentEvent [postId={}, commentId={}] to topic={}",
                                event.postId(),
                                event.commentId(),
                                topic,
                                ex);
                    }
                });
    }
}
