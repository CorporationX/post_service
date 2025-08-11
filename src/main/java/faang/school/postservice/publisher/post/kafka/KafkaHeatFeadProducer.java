package faang.school.postservice.publisher.post.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHeatFeadProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(KafkaHeatFeedSizeDto kafkaHeatFeedSizeDto, String topic) {
        kafkaTemplate.send(topic, String.valueOf(System.currentTimeMillis()), kafkaHeatFeedSizeDto);
        log.info("Message {} has been sent.", kafkaHeatFeedSizeDto);
    }
}
