package faang.school.postservice.publisher;

import faang.school.postservice.dto.kafka.NewCommentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentProducer {
    private final KafkaTemplate<String, NewCommentEvent> kafkaTemplate;
    @Value("${spring.kafka.topics.comments-topic}")
    private String commentsTopic;

    @Async("kafkaThreadPool")
    public void publishCommentEvent(NewCommentEvent kafkaCommentEvent) {
        kafkaTemplate.send(commentsTopic, kafkaCommentEvent);
    }
}
