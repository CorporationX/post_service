package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaLikeEventDto;
import faang.school.postservice.kafka.AbstractKafkaEventProducer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeEventProducer extends AbstractKafkaEventProducer<KafkaLikeEventDto> {

    @Getter
    @Value("${spring.kafka.topics.like-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaLikeEventDto> kafkaTemplate;

    protected KafkaTemplate<String, KafkaLikeEventDto> getKafkaTemplate() {
        return kafkaTemplate;
    }
}
