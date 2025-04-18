package faang.school.postservice.publisher.post.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.model.event.post.view.NotificationPostViewEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationPostViewEventPublisher implements EventPublisher<NotificationPostViewEvent> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${spring.kafka.topics.notification-post-view-topic.name}")
    private String topic;


    @Override
    public void publish(NotificationPostViewEvent event) {
        try{
            String json = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, json);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    @Override
    public Class<NotificationPostViewEvent> getEventType() {
        return NotificationPostViewEvent.class;
    }
}
