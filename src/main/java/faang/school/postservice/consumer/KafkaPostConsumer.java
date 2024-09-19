package faang.school.postservice.consumer;

import faang.school.postservice.dto.event.kafka.NewPostEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@KafkaListener(topics = "${spring.kafka.topic.new-post}",
        groupId = "${spring.kafka.consumer.group-id}")
public class KafkaPostConsumer {
    private final
    public void listen(NewPostEvent event) {
        log.info("New post event received: {}", event);

    }
}
