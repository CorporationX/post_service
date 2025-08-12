package faang.school.postservice.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import faang.school.postservice.dto.kafka.KafkaHeatFeedSizeDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaHeatPostConsumer {
    @KafkaListener(topics = "${kafka.topics.heatPostRequest}", groupId = "my-consumer-group")
    public void counsumePostEvent(KafkaHeatFeedSizeDto kafkaHeatFeedSizeDto) {
        log.info("Message {} has been received from Kakfa.", kafkaHeatFeedSizeDto);
    }
}
