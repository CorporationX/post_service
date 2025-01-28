package faang.school.postservice.publisher;

import faang.school.postservice.dto.CommentDto;
import faang.school.postservice.events.CommentEvent;
import faang.school.postservice.events.LikeEvent;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class CommentEventPublisher {
    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic-name.comments}")
    private String commentsTopic;

    public void sendMessage(CommentEvent comment) {
        kafkaTemplate.send(commentsTopic, comment);
    }
}
