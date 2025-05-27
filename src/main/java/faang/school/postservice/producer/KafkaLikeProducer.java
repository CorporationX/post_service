package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.dto.event.LikeEventDto;
import faang.school.postservice.properties.KafkaProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaLikeProducer {

    private final GenericKafkaProducer<LikeEventDto> producer;
    private final KafkaProperties kafkaProperties;

    public void sendLikeEvent(LikeEventDto event) {
        String topic = kafkaProperties.getTopics().getLikes();
        producer.sendEvent(topic, event, "LikeEvent");
    }
}
