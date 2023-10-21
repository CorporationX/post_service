package faang.school.postservice.messaging.kafka.publishing.like;

import faang.school.postservice.messaging.kafka.events.LikeEvent;
import faang.school.postservice.messaging.kafka.publishing.AbstractProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LikeProducer extends AbstractProducer<LikeEvent> {
    @Value("${spring.kafka.channels.like_event_channel.name}")
    private String topic;

    @Autowired
    public LikeProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void publish(LikeEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
