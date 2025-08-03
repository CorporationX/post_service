package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentEventProducer {

    @Value("${spring.kafka.topics.comment-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaCommentEventDto> kafkaTemplate;

    public void sendMessage(KafkaCommentEventDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }
}