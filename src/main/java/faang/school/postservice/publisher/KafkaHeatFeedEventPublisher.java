package faang.school.postservice.publisher;


import faang.school.postservice.dto.kafkaevents.FeedHeatEvent;
import faang.school.postservice.exception.KafkaEventPublishException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaHeatFeedEventPublisher {
    private final KafkaTemplate<String, FeedHeatEvent> kafkaTemplate;

    @Value("${spring.data.kafka.topic.heat}")
    private String heatFeedTopic;

    public void publish(FeedHeatEvent event) {
        try {
            kafkaTemplate.send(heatFeedTopic, event);
            log.info("Событие для пользователя {} отправлено", event.getId());
        } catch (Exception e) {
            log.error("Не удалось отправить событие для пользователя {} ", event.getId());
            throw new KafkaEventPublishException("Не удалось отправить событие", e);
        }

    }
}
