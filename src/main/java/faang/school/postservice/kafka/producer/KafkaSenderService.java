package faang.school.postservice.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class KafkaSenderService implements KafkaSender {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    @SneakyThrows
    @Async("kafkaPublisherExecutor")
    public void sendMessage(String topicName, Object message) {
        log.info("Sending message: {}, to topic: {}", topicName, message.toString());
        kafkaTemplate.send(topicName, message);
    }
}
