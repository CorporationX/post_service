package faang.school.postservice.consumer.post;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import faang.school.postservice.dto.kafka.KafkaPostMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaConsumer {
    @KafkaListener(topics = "${kafka.topics.post-create-event}", groupId = "my-consumer-group")
    public void counsumePostEvent(KafkaPostMessage kafkaPostMessage) {
        log.info("Message {} has been received from Kakfa.", kafkaPostMessage);
    }
}
