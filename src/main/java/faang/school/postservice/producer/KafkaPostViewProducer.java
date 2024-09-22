package faang.school.postservice.producer;

import faang.school.postservice.dto.event.kafka.PostViewEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaPostViewProducer extends AbstractProducer<PostViewEvent>
        implements KafkaProducer<PostViewEvent> {

    @Value("${spring.kafka.topic.post-views}")
    private String topic;

    public KafkaPostViewProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    @Override
    public void send(PostViewEvent event) {
        super.sendMessage(topic, event);
    }
}
