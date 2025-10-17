package faang.school.postservice.publisher;

import faang.school.postservice.dto.notification.CommentAnalytics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;


@Component
public class CommentEventPublisher extends AbstractEventPublisher<CommentAnalytics> {

    public CommentEventPublisher(
            @Value(value = "${spring.kafka.topics.comment.comment-topic.name}") String topic,
            KafkaTemplate<String, CommentAnalytics> kafkaTemplate) {
        super(topic, kafkaTemplate);
    }
}