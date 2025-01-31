package faang.school.postservice.producer;

import faang.school.postservice.event.PostCommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaPostCommentProducer implements KafkaPublisher<PostCommentEvent> {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic postCommentKafkaTopic;

    @Override
    public void publish(PostCommentEvent event) {
        kafkaTemplate.send(postCommentKafkaTopic.name(), event);
        log.info("Comment event was sent to Kafka topic {}: {} ", postCommentKafkaTopic.name(), event);
    }
}
