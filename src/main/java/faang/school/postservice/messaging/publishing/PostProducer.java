package faang.school.postservice.messaging.publishing;

import faang.school.postservice.messaging.events.PostPublishedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostProducer implements Publishable<PostPublishedEvent> {

    @Value("${spring.kafka.channels.post_event_channel.name}")
    private String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(PostPublishedEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
