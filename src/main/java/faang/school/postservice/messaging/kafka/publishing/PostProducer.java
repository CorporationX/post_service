package faang.school.postservice.messaging.kafka.publishing;

import faang.school.postservice.messaging.events.PostPublishedEvent;
import faang.school.postservice.messaging.kafka.events.PostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PostProducer extends AbstractProducer<PostPublishedEvent> {
    @Value("${spring.kafka.channels.post_event_channel.name}")
    private String topic;

    @Autowired
    public PostProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        super(kafkaTemplate);
    }

    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(topic, event);
    }

}
