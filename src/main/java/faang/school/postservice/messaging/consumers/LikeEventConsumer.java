package faang.school.postservice.messaging.consumers;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Data
public class LikeEventConsumer {

    @KafkaListener(topics = "like-publication")
    public void listen() {

    }
}
