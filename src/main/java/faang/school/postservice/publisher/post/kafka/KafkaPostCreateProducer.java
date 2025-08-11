package faang.school.postservice.publisher.post.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import faang.school.postservice.config.kafka.KafkaTopicsConfig;
import faang.school.postservice.dto.kafka.KafkaPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPostCreateProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsConfig kafkaTopicsConfig;

    public void sendMessage(KafkaPostMessage kafkaPostMessage) {
        kafkaTemplate.send(kafkaTopicsConfig.getPostCreateEvent(), kafkaPostMessage.getId().toString(), kafkaPostMessage);
        log.info("Message {} has been sent.", kafkaPostMessage);
    }
}
