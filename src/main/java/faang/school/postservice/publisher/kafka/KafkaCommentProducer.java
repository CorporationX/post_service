package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.kafka.KafkaCommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaCommentProducer extends AbstractKafkaProducer<KafkaCommentEvent> {

    @Value("${spring.kafka.topics.comment.name}")
    private String commentTopic;

    public void publishKafkaEvent(KafkaCommentEvent comment) {
        publishKafkaEvent(comment, commentTopic);
    }

}