package faang.school.postservice.kafka;

import faang.school.postservice.dto.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic topic;

    protected void sendEvent(EventDto eventDto) {
        kafkaTemplate.send(topic.name(), eventDto);
    }
}
