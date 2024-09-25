package faang.school.postservice.producer;

import faang.school.postservice.dto.HeaterEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaHeaterFeedProducer extends AbstractProducer<HeaterEvent> {
    @Value("${spring.kafka.topic.heater-feed}")
    private String heaterFeesTopicName;

    public KafkaHeaterFeedProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void send(HeaterEvent heaterEvent) {
        super.sendMessage(heaterFeesTopicName, heaterEvent);
    }
}
