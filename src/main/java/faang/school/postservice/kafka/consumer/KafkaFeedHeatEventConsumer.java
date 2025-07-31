package faang.school.postservice.kafka.consumer;

import faang.school.postservice.dto.kafka.KafkaFeedHeatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaFeedHeatEventConsumer {
    @Value("${spring.kafka.topics.feed-heat-event}")
    public final String topic;

    @KafkaListener(topics = "#{__listener.topic}", groupId = "feed-heat-group")
    public void listen(KafkaFeedHeatDto dto) {
        System.out.println("Received message: " + dto);
    }
}
