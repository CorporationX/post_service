package faang.school.postservice.messaging.publishing;

import faang.school.postservice.messaging.events.PostEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostProducer implements Publishable<PostEvent> {

    @Value("${spring.kafka.channels.post_event_channel.name}")
    private final String topic;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(PostEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
