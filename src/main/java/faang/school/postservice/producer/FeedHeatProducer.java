package faang.school.postservice.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.postservice.config.kafka.KafkaProperty;
import faang.school.postservice.dto.event.FeedHeatEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FeedHeatProducer extends KafkaProducer<FeedHeatEvent> {
    private final KafkaProperty kafkaProps;

    public FeedHeatProducer(ObjectMapper objectMapper,
                            KafkaProperty kafkaProperty,
                            KafkaTemplate<String, String> kafkaTemplate) {
        super(objectMapper, kafkaTemplate);
        this.kafkaProps = kafkaProperty;
    }

    @Override
    public void sendEvent(FeedHeatEvent event) {
        log.info("Sending event of heat feed for userId = {}", event.userId());
        sendTo(kafkaProps.topic().feedHeat(), event);
    }
}
