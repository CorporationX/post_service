package faang.school.postservice.config.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.comment.CommentEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationKafkaProducer extends AbstractKafkaProducer {
    public NotificationKafkaProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        super(kafkaTemplate, objectMapper);
    }

    @Value("${spring.kafka.producer.topics.notification}")
    private String notificationTopic;

    public void sendNotificationComment(CommentEvent commentEvent) {
        send(notificationTopic,commentEvent);
    }
}
