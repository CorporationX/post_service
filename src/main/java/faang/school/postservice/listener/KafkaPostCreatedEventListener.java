package faang.school.postservice.listener;

import faang.school.postservice.event.PostCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaPostCreatedEventListener extends AbstractKafkaListener<PostCreatedEvent> {

    @KafkaListener(topics = "${spring.data.kafka.posts-topic}", groupId = "test")
    public void listen(String data) {
        consume(data, PostCreatedEvent.class, this::handle);
    }

    @Override
    public void handle(PostCreatedEvent event) {
        log.info("Received post created event: {}", event);
    }
}
