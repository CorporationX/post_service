package faang.school.postservice.messaging.consumers;


import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class PostEventConsumer {

    @KafkaListener(
            topics = "post-publication",
            groupId = "group"
    )
    public void listen() {

    }
}
