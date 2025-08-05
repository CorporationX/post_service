package faang.school.postservice.publisher.post.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import faang.school.postservice.dto.kafka.KafkaPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, KafkaPostMessage> kafkaTemplate;

    public void sendMessage(KafkaPostMessage kafkaPostMessage) {
        kafkaTemplate.send("post-create-event", kafkaPostMessage.getId().toString(), kafkaPostMessage);
        log.info("Message {} has been sent.", kafkaPostMessage);
    }
}
