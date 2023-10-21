package faang.school.postservice.publisher.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessagePublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publish(String channel, Object message) {
        kafkaTemplate.send(channel, message);
    }
}
