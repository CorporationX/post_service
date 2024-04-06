package faang.school.postservice.publisher;

import faang.school.postservice.dto.event.HeatFeedKafkaEventDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class KafkaHeatFeedProducer extends AbstractEventProducer<HeatFeedKafkaEventDto> {
    @Value("${spring.kafka.topics.heat-feed.name}")
    private String topicName;

    public KafkaHeatFeedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Async("executorService")
    public void sendAsyncHeatFeedEvent(HeatFeedKafkaEventDto event) {
        sendEvent(event, topicName);
    }
}