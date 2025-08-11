package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaLikeEventDto;
import faang.school.postservice.kafka.KafkaEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeEventProducer implements KafkaEventProducer<KafkaLikeEventDto> {

    @Value("${spring.kafka.topics.like-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaLikeEventDto> kafkaTemplate;

    @Override
    public void sendMessage(KafkaLikeEventDto messageDto) {
        kafkaTemplate.send(topic, messageDto);
    }
}
