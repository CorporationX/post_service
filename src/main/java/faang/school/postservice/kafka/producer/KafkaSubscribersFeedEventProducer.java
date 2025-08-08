package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.kafka.KafkaSubscribersFeedHeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaSubscribersFeedEventProducer {

    @Value("${spring.kafka.topics.subscribers-feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaSubscribersFeedHeatDto> kafkaTemplate;

    public void sendMessage(Long userId, List<Long> followerIds) {
        kafkaTemplate.send(topic, new KafkaSubscribersFeedHeatDto(userId, followerIds));
    }
}
