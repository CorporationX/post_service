package faang.school.postservice.publisher;

import faang.school.postservice.config.kafka.KafkaCommentTopicConfigurationProperties;
import faang.school.postservice.event.comment.CommentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentPublisher extends AbstractEventPublisher<CommentEvent> {
    public CommentPublisher(
            @Value("${spring.kafka.topics.comments.name}") String topic,
            KafkaTemplate<String, CommentEvent> kafkaTemplate
    ) {
        super(topic, kafkaTemplate);
    }
}