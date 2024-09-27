package faang.school.postservice.kafka;

import faang.school.postservice.dto.event.PostPublishedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Consumer {
    @KafkaListener(topics = "${spring.kafka.topic.post.published}", groupId = "${spring.kafka.consumer.group-id.post}")
    void listener(PostPublishedEvent event, Acknowledgment ack) {
        log.info("Received postPublishedEvent [{}]", event.toString());
        ack.acknowledge();
    }
}
