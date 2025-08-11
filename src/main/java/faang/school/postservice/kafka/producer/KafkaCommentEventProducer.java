package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentEventProducer implements KafkaEventProducer<KafkaCommentEventDto> {

    @Value("${spring.kafka.topics.comment-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaCommentEventDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaCommentEventDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }

}