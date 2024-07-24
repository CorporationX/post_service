package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.EventDto;
import faang.school.postservice.dto.event.FeedHeatEventDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaFeedHeatEventProducer extends AbstractEventProducer {

    public KafkaFeedHeatEventProducer(
            KafkaTemplate<String, EventDto> kafkaTemplate,
            NewTopic feedHeatKafkaTopic) {
        super(kafkaTemplate, feedHeatKafkaTopic);
    }

    public void handleFeedHeating(FeedHeatEventDto heatEvent) {
        sendEvent(heatEvent, String.valueOf(heatEvent));
    }
}
