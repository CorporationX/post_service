package faang.school.postservice.kafka.producer;

import faang.school.postservice.dto.event.EventDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public abstract class AbstractEventProducer {
    private final KafkaTemplate<String, EventDto> kafkaTemplate;
    private final NewTopic topic;

    protected void sendEvent(EventDto eventDto, String key) {
        kafkaTemplate.send(topic.name(), key, eventDto);
    }
}
