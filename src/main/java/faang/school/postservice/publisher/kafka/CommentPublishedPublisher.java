package faang.school.postservice.publisher.kafka;

import faang.school.postservice.dto.comment.CommentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentPublishedPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    @Value("${spring.data.kafka.topics.comment}")
    private String topic;

    public void publish(CommentDto commentDto) {
        kafkaTemplate.send(topic, commentDto);
    }
}
