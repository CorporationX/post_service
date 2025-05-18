package faang.school.postservice.service.kafka.publisher;

import faang.school.postservice.utils.JsonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaPublisher {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final JsonUtils jsonUtils;

    public void send(String topic, Object data) {
        kafkaTemplate.send(topic, jsonUtils.serialize(data));
        log.info("Sent message: {} to topic {}", jsonUtils.serialize(data), topic);
    }
}
