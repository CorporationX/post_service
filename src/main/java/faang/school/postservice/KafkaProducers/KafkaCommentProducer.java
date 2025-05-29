package faang.school.postservice.KafkaProducers;

import faang.school.postservice.dto.comment.CommentSendEvent;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaCommentProducer {

    private final KafkaTemplate<String, CommentSendEvent> kafkaTemplate;

    @Value("${app.kafka.topic.comments}")
    private String commentTopic;

    public void sendCommentCreatedEvent(CommentSendEvent event) {
        kafkaTemplate.send(commentTopic, event);
    }

    @Bean
    public NewTopic commentsTopic(@Value("${app.kafka.topic.comments}") String topicName) {
        return TopicBuilder.name(topicName).partitions(3).replicas(1).build();
    }
}
