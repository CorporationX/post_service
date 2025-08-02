package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.feed.UserFeedHeatDto;
import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventProducer {

    @Value("${spring.kafka.topics.feed-heat-event}")
    private final String topic;
    private final KafkaTemplate<String, KafkaFeedHeatDto> kafkaTemplate;

    public void sendMessage(List<UserFeedHeatDto> userFeedHeatDtos) {
        kafkaTemplate.send(topic, new KafkaFeedHeatDto(userFeedHeatDtos));
    }
}
