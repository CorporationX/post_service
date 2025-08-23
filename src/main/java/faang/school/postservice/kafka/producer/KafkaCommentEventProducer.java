package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaCommentEventDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaCommentEventProducer extends AbstractKafkaEventProducer<KafkaCommentEventDto> {

    @Getter
    @Value("${spring.kafka.topics.comment-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaCommentEventDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaCommentEventDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
