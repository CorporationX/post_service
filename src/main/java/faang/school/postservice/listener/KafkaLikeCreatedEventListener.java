package faang.school.postservice.listener;

import faang.school.postservice.event.LikeCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaLikeCreatedEventListener extends AbstractKafkaListener<LikeCreatedEvent> {

    @Value("${spring.data.kafka.likes-topic}")
    private String likesTopic;

    @KafkaListener(topics = "${spring.data.kafka.likes-topic}", groupId = "likes")
    public void listen(String message) {
        consume(message, LikeCreatedEvent.class, this::handle);
    }

    @Override
    public void handle(LikeCreatedEvent event) {
        log.info("Like created: {}", event);
    }
}
